package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.jose.SecretKeyDerivation;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.*;
import net.minidev.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.mapping.OpenIdSubjectMapper;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.common.sso._1_0.protocol.SubjectType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import sun.security.provider.X509Factory;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;

/**
 * We use the new nimbus API
 */
public class ProxyRPAuthzTokenConsumerProducer extends AbstractAuthzTokenConsumerProducer {

    private static final Log logger = LogFactory.getLog(ProxyRPAuthzTokenConsumerProducer.class);

    private static final int MAX_NUM_OF_USER_INFO_RETRIES = 3;

    public ProxyRPAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcessAuthzTokenResponse(CamelMediationExchange exchange, AuthenticationResponse authnResp) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        if (!authnResp.indicatesSuccess()) {
            AuthenticationErrorResponse errorResp = authnResp.toErrorResponse();
            String msg = toErrorString(errorResp.getErrorObject());
            logger.debug(msg);
            throw new OpenIDConnectException(msg);
        }

        // TODO !
        validateRequest(mediator, authnResp);

        OIDCTokenResponse resp = this.resolveToken(authnResp.toSuccessResponse(), mediationState, mediator);

        SPAuthnResponseType ssoResponse = new SPAuthnResponseType();
        ssoResponse.setID(uuidGenerator.generateId());
        ssoResponse.setIssuer(getFederatedProvider().getName());
        SPInitiatedAuthnRequestType ssoRequest =
                (SPInitiatedAuthnRequestType) in.getMessage().getState().
                        getLocalVariable("urn:org:atricore:idbus:sso:protocol:SPInitiatedAuthnRequest");

        if (ssoRequest != null) {
            ssoResponse.setInReplayTo(ssoRequest.getID());
        }

        // Map the OIDC Claims to our subject
        OpenIdSubjectMapper mapper = mediator.getSubjectMapperFactory().newInstance(getFederatedProvider(), resp.getOIDCTokens());
        SubjectType subject = mapper.toSubject();

        ssoResponse.setSessionIndex(sessionUuidGenerator.generateId());
        ssoResponse.setSubject(subject);
        ssoResponse.getSubjectAttributes().addAll(mapper.getAttributes());

        // ------------------------------------------------------------------------------
        // Send SP Authentication response
        // ------------------------------------------------------------------------------
        SPInitiatedAuthnRequestType authnRequest = (SPInitiatedAuthnRequestType) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:authnRequest");

        // Send response back
        String destinationLocation = resolveSpProxyACS(authnRequest);

        if (logger.isTraceEnabled())
            logger.trace("Sending response to " + destinationLocation);

        EndpointDescriptor destination =
                new EndpointDescriptorImpl("EmbeddedSPAcs",
                        "AssertionConsumerService",
                        OpenIDConnectBinding.SSO_ARTIFACT.getValue(),
                        destinationLocation, null);

        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPAuthnResponse", "", destination, in.getMessage().getState()));

        exchange.setOut(out);

        return;
    }

    protected OIDCTokenResponse resolveToken(AuthenticationSuccessResponse response,
                                             MediationState mediationState,
                                             OpenIDConnectProxyMediator mediator) throws OpenIDConnectException {
        try {
            // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)

            // -------------------------------------------------
            // Load shared secret
            // Use SHA-1 to generate a hash from your key and trim the result to 256 bit (32 bytes)
            Secret secret = new Secret(mediator.getClientSecret());
            SecretKey secretKey = SecretKeyDerivation.deriveSecretKey(secret, 256);

            URI tokenEndpoint = new URI(mediator.getAuthzTokenServiceLocation());

            ClientAuthentication clientAuth = null;
            ClientID clientId = new ClientID(mediator.getClientId());

            // -------------------------------------------------
            // Build client authentication based on provider metadata
            // -------------------------------------------------
            if (mediator.getMetadata() != null) {
                List<ClientAuthenticationMethod> supportedMethods =
                        mediator.getMetadata().getTokenEndpointAuthMethods();

                if (supportedMethods != null && !supportedMethods.isEmpty()) {
                    logger.debug("Provider supports auth methods: " + supportedMethods);

                    // Try to use the first supported method that we can handle
                    ClientAuthenticationMethod selectedMethod = null;
                    for (ClientAuthenticationMethod method : supportedMethods) {
                        if (method.equals(ClientAuthenticationMethod.CLIENT_SECRET_POST) ||
                                method.equals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC) ||
                                method.equals(ClientAuthenticationMethod.NONE)) {
                            selectedMethod = method;
                            break;
                        }
                    }

                    if (selectedMethod != null) {
                        clientAuth = createClientAuthentication(clientId, secret, selectedMethod);
                        logger.info("Using token endpoint auth method: " + selectedMethod);
                    } else {
                        logger.warn("No compatible auth method found in metadata, defaulting to client_secret_basic");
                        clientAuth = new ClientSecretBasic(clientId, secret);
                    }
                } else {
                    logger.warn("Metadata present but no auth methods specified, defaulting to client_secret_basic");
                    clientAuth = new ClientSecretBasic(clientId, secret);
                }
            } else {
                // No metadata available, use default
                logger.debug("No metadata available, using default client_secret_basic");
                clientAuth = new ClientSecretBasic(clientId, secret);
            }

            // -------------------------------------------------
            // Build Token request
            // -------------------------------------------------
            AuthorizationCode code = response.getAuthorizationCode();
            EndpointDescriptor ed = resolveAuthnResponseEndpoint();
            URI redirectUri = new URI(ed.getLocation());

            // Authorization Grant
            AuthorizationGrant authzGrant = new AuthorizationCodeGrant(code, redirectUri);

            // Scopes
            Scope scope = Scope.parse(mediator.getScopes());

            logger.debug("Token request - Endpoint: " + tokenEndpoint);
            logger.debug("Token request - Redirect URI: " + redirectUri);
            logger.debug("Token request - Auth method: " +
                    (clientAuth != null ? clientAuth.getMethod() : "none"));

            TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);

            HTTPResponse httpTokenResponse = tokenRequest.toHTTPRequest().send();

            logger.debug("Token response - Status: " + httpTokenResponse.getStatusCode());
            logger.debug("Token response - Content-Type: " + httpTokenResponse.getContentType());

            JSONObject jsonObject = httpTokenResponse.getContentAsJSONObject();

            if (httpTokenResponse.getStatusCode() != HTTPResponse.SC_OK) {
                // We got an error response...
                TokenErrorResponse errorResponse = TokenErrorResponse.parse(jsonObject);
                String errMsg = toErrorString(errorResponse.getErrorObject());
                logger.error("Token endpoint error: " + errMsg);
                throw new OpenIDConnectException(errMsg);

            } else {
                OIDCTokenResponse successResponse = OIDCTokenResponse.parse(jsonObject);
                Nonce nonce = (Nonce) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:nonce");
                State state = new State(mediationState.getLocalState().getId());

                JWTClaimsSet claims = null;

                // Get the tokens from response
                AccessToken accessToken = successResponse.getOIDCTokens().getAccessToken();
                RefreshToken refreshToken = successResponse.getOIDCTokens().getRefreshToken();
                BearerAccessToken bearerAccessToken = successResponse.getOIDCTokens().getBearerAccessToken();
                JWT idToken = successResponse.getOIDCTokens().getIDToken();

                // -------------------------------------------------
                // Verify ID Token signature
                // -------------------------------------------------
                SignedJWT signedIdToken = (SignedJWT) idToken;
                JWSVerifier verifier = createVerifier(signedIdToken, mediator);

                if (!signedIdToken.verify(verifier)) {
                    throw new OpenIDConnectException("ID Token signature verification failed");
                }

                logger.debug("ID Token signature verified successfully");

                // Get claims from verified token
                claims = signedIdToken.getJWTClaimsSet();

                // -------------------------------------------------
                // Verify nonce
                // -------------------------------------------------
                String nonceStr = (String) claims.getClaim("nonce");
                if (nonce != null) {
                    if (nonceStr == null || !nonce.getValue().equals(nonceStr)) {
                        throw new OpenIDConnectException("Invalid NONCE : " + nonceStr);
                    }
                }

                // -------------------------------------------------
                // Verify standard claims
                // -------------------------------------------------

                // Check expiration
                if (claims.getExpirationTime() != null &&
                        claims.getExpirationTime().before(new Date())) {
                    throw new OpenIDConnectException("ID Token has expired");
                }

                // Check issuer
                String expectedIssuer = mediator.getIssuer();
                if (expectedIssuer != null && !expectedIssuer.equals(claims.getIssuer())) {
                    throw new OpenIDConnectException("Invalid issuer. Expected: " + expectedIssuer +
                            ", Got: " + claims.getIssuer());
                }

                // Check audience (should contain client ID)
                List<String> audience = claims.getAudience();
                if (audience == null || !audience.contains(mediator.getClientId())) {
                    throw new OpenIDConnectException("Invalid audience. Client ID not found in token audience");
                }

                // Check issued at time (token shouldn't be from the future)
                if (claims.getIssueTime() != null &&
                        claims.getIssueTime().after(new Date(System.currentTimeMillis() + 60000))) {
                    throw new OpenIDConnectException("ID Token issued in the future");
                }

                logger.debug("All ID Token validations passed");

                return successResponse;
            }

        } catch (OpenIDConnectException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenIDConnectException(e.getMessage(), e);
        }
    }

    /**
     * Creates a ClientAuthentication based on the specified method
     */
    private ClientAuthentication createClientAuthentication(
            ClientID clientId,
            Secret secret,
            ClientAuthenticationMethod method) {

        if (method == null) {
            logger.warn("Auth method is null, defaulting to client_secret_basic");
            return new ClientSecretBasic(clientId, secret);
        }

        if (method.equals(ClientAuthenticationMethod.CLIENT_SECRET_POST)) {
            logger.debug("Creating ClientSecretPost authentication");
            return new ClientSecretPost(clientId, secret);
        } else if (method.equals(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)) {
            logger.debug("Creating ClientSecretBasic authentication");
            return new ClientSecretBasic(clientId, secret);
        } else if (method.equals(ClientAuthenticationMethod.NONE)) {
            logger.debug("Using no client authentication (public client)");
            return null;
        } else if (method.equals(ClientAuthenticationMethod.CLIENT_SECRET_JWT)) {
            throw new UnsupportedOperationException(
                    "client_secret_jwt authentication method is not yet implemented");
        } else if (method.equals(ClientAuthenticationMethod.PRIVATE_KEY_JWT)) {
            throw new UnsupportedOperationException(
                    "private_key_jwt authentication method is not yet implemented");
        } else {
            logger.warn("Unknown auth method: " + method + ", defaulting to client_secret_basic");
            return new ClientSecretBasic(clientId, secret);
        }
    }

    /**
     * Creates a JWS verifier based on available key material
     */
    private JWSVerifier createVerifier(SignedJWT signedJWT, OpenIDConnectProxyMediator mediator)
            throws Exception {

        String kid = signedJWT.getHeader().getKeyID();
        JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();

        logger.debug("Creating verifier for kid: " + kid + ", algorithm: " + algorithm);

        // -------------------------------------------------
        // Priority 1: Use JWK Set if available (modern approach)
        // -------------------------------------------------
        if (mediator.getJwkSet() != null) {
            JWKSet jwkSet = mediator.getJwkSet();

            // Try to find key by kid
            JWK jwk = null;
            if (kid != null) {
                jwk = jwkSet.getKeyByKeyId(kid);
                if (jwk == null) {
                    logger.warn("No key found with kid: " + kid + ", will try all keys");
                }
            }

            // If no kid or key not found, try all keys
            if (jwk == null) {
                logger.debug("Attempting to verify with all available keys");
                for (JWK candidateKey : jwkSet.getKeys()) {
                    try {
                        JWSVerifier candidateVerifier = createVerifierFromJWK(candidateKey, algorithm);
                        if (signedJWT.verify(candidateVerifier)) {
                            logger.debug("Successfully verified with key: " + candidateKey.getKeyID());
                            return candidateVerifier;
                        }
                    } catch (Exception e) {
                        logger.trace("Failed to verify with key " + candidateKey.getKeyID(), e);
                    }
                }
                throw new OpenIDConnectException("Unable to verify signature with any key from JWK Set");
            }

            // Create verifier from found JWK
            return createVerifierFromJWK(jwk, algorithm);
        }

        // -------------------------------------------------
        // Priority 2: Use legacy server key (X.509 certificate)
        // -------------------------------------------------
        String publicKeyContent = mediator.getServerKey();
        if (publicKeyContent != null && !publicKeyContent.trim().isEmpty()) {
            logger.debug("Using legacy X.509 certificate for verification");

            byte[] publicKeyContentBytes = Base64.decodeBase64(
                    publicKeyContent
                            .replaceAll(X509Factory.BEGIN_CERT, "")
                            .replaceAll(X509Factory.END_CERT, "")
                            .trim()
                            .getBytes()
            );

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate cert = cf.generateCertificate(new ByteArrayInputStream(publicKeyContentBytes));
            PublicKey pubKey = cert.getPublicKey();

            return new RSASSAVerifier((RSAPublicKey) pubKey);
        }

        // -------------------------------------------------
        // Priority 3: Use HMAC with client secret (symmetric signature)
        // -------------------------------------------------
        if (algorithm.getName().startsWith("HS")) {
            logger.debug("Using HMAC verification with client secret");
            Secret secret = new Secret(mediator.getClientSecret());
            SecretKey secretKey = SecretKeyDerivation.deriveSecretKey(secret, 256);
            return new MACVerifier(secretKey);
        }

        throw new OpenIDConnectException(
                "No suitable key material found for signature verification. " +
                        "Configure either JWK Set URI, server certificate, or use HMAC algorithm."
        );
    }

    /**
     * Creates a JWS verifier from a JWK based on the key type
     */
    private JWSVerifier createVerifierFromJWK(JWK jwk, JWSAlgorithm algorithm) throws Exception {

        if (jwk instanceof RSAKey) {
            return new RSASSAVerifier((RSAKey) jwk);
        } else if (jwk instanceof ECKey) {
            return new ECDSAVerifier((ECKey) jwk);
        } else if (jwk instanceof OctetSequenceKey) {
            return new MACVerifier((OctetSequenceKey) jwk);
        }

        throw new OpenIDConnectException(
                "Unsupported JWK key type: " + jwk.getKeyType() +
                        " for key ID: " + jwk.getKeyID()
        );
    }

    protected void validateRequest(OpenIDConnectProxyMediator mediator, AuthenticationResponse authnResp) {
        // TODO : !!!
    }

    protected String toErrorString(ErrorObject error) {
        return "OIDC error: [" + error.getCode() + "] " +
                (error.getURI() != null ? error.getURI() : "") +
                (error.getDescription() != null ? " :" + error.getDescription() : "") +
                " [HTTP STATUS:" + error.getHTTPStatusCode() + "]";
    }
}
