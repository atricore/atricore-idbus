package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
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
import org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.mapping.OpenIdSubjectMapperFactory;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.capabilities.sso.support.core.NameIDFormat;
import org.atricore.idbus.common.sso._1_0.protocol.*;
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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
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

            // -------------------------------------------------
            // Support RSA keys:
            // Load IDP RSA Public key from a DER file
            String publicKeyContent = mediator.getServerKey();

            // Build signature verifier
            JWSVerifier verifier = null;
            if (publicKeyContent != null) {
                byte[] publicKeyContentBytes = Base64.decodeBase64(publicKeyContent.replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, "").getBytes());

                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                Certificate cert = cf.generateCertificate(new ByteArrayInputStream(publicKeyContentBytes));
                PublicKey pubKey = cert.getPublicKey();
                verifier = new RSASSAVerifier((RSAPublicKey) pubKey);
             } else {
                // EC (ES256,etc. ) Signature check
                // JWSVerifier verifier = new ECDSAVerifier(publicKey);
                // HMAC
                verifier = new MACVerifier(secretKey);
            }

            // Load IDP RSA Public key from a pub key file
            /*
            String publicKeyContent = "MIIDBTCCAe2gAwIBAgIQQiR8gZNKuYpH6cP+KIE5ijANBgkqhkiG9w0BAQsFADAtMSswKQYDVQQDEyJhY2NvdW50cy5hY2Nlc3Njb250cm9sLndpbmRvd3MubmV0MB4XDTIwMDgyODAwMDAwMFoXDTI1MDgyODAwMDAwMFowLTErMCkGA1UEAxMiYWNjb3VudHMuYWNjZXNzY29udHJvbC53aW5kb3dzLm5ldDCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMkymupuRhTpZc+6CBxQpL0SaAb+8CzLiiDyx2xRoecjojvKN2pKKjIX9cejMSDRoWaOnZCK4VZVX1iYRCWT1WkHb8r1ZpSGa7oXG89zxjKjwG46tiamwdZjJ7Mhh8fqLz9ApucY/LICPMJuu6d56LKs6hb4OpjylTvsNUAa+bHg1NgMFNg0fPCxdr9N2Y4J+Jhrz3VDl4oU0KDZX/pyRXblzA8kYGWm50dh5WB4WoB8MtW3lltVrRGj8/IgTf9GxpBsO9OWgwVByZHU7ctZs7AmUbq/59Ipql7vSM6EsoquXdMiq0QOcZAPitwzHkTKrmeULz0/RHnuBGXxS/e8wX0CAwEAAaMhMB8wHQYDVR0OBBYEFGcWXwaqmO25Blh2kHHAFrM/AS2CMA0GCSqGSIb3DQEBCwUAA4IBAQDFnKQ98CBnvVd4OhZP0KpaKbyDv93PGukE1ifWilFlWhvDde2mMv/ysBCWAR8AGSb1pAW/ZaJlMvqSN/+dXihcHzLEfKbCPw4/Mf2ikq4gqigt5t6hcTOSxL8wpe8OKkbNCMcU0cGpX5NJoqhJBt9SjoD3VPq7qRmDHX4h4nniKUMI7awI94iGtX/vlHnAMU4+8y6sfRQDGiCIWPSyypIWfEA6/O+SsEQ7vZ/b4mXlghUmxL+o2emsCI1e9PORvm5yc9Y/htN3Ju0x6ElHnih7MJT6/YUMISuyob9/mbw8Vf49M7H2t3AE5QIYcjqTwWJcwMlq5i9XfW2QLGH7K5i8";
            byte [] publicKeyContentBytes = Base64.decodeBase64(publicKeyContent.replaceAll("-----BEGIN PUBLIC KEY-----", "").replaceAll("-----END PUBLIC KEY-----", "").getBytes());

            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKeyContentBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey pubKey = kf.generatePublic(keySpecX509);
            */

            URI tokenEndpoint = new URI(mediator.getAuthzTokenServiceLocation());

            // TODO : Support other client authentication types
            ClientAuthentication clientAuth = null;

            // -------------------------------------------------
            // Build  client authentication (client_secret_basic)
            {
                ClientID clientId = new ClientID(mediator.getClientId());
                clientAuth = new ClientSecretBasic(clientId, secret);
            }
            // -------------------------------------------------
            // Build Token request
            AuthorizationCode code = response.getAuthorizationCode();
            EndpointDescriptor ed = resolveAuthnResponseEndpoint();
            URI redirectUri = new URI(ed.getLocation());

            // Authorization Grant
            AuthorizationGrant authzGrant = new AuthorizationCodeGrant(code, redirectUri);

            // Scopes
            Scope scope = Scope.parse(mediator.getScopes());

            TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, authzGrant, scope);

            HTTPResponse httpTokenResponse = tokenRequest.toHTTPRequest().send();
            JSONObject jsonObject = httpTokenResponse.getContentAsJSONObject();

            if (httpTokenResponse.getStatusCode() != HTTPResponse.SC_OK) {
                // We got an error response...
                TokenErrorResponse errorResponse = TokenErrorResponse.parse(jsonObject);
                String errMsg = toErrorString(errorResponse.getErrorObject());
                logger.debug(errMsg);
                throw new OpenIDConnectException(errMsg);

            } else {
                OIDCTokenResponse successResponse = OIDCTokenResponse.parse(jsonObject);
                Nonce nonce = (Nonce) mediationState.getLocalVariable("urn:OPENID-CONNECT:1.0:nonce");
                State state = new State(mediationState.getLocalState().getId());

                JWTClaimsSet claims = null;

                // Get the access token, the server may also return a refresh token
                AccessToken accessToken = successResponse.getOIDCTokens().getAccessToken();
                RefreshToken refreshToken = successResponse.getOIDCTokens().getRefreshToken();
                BearerAccessToken bearerAccessToken = successResponse.getOIDCTokens().getBearerAccessToken();
                JWT idToken = successResponse.getOIDCTokens().getIDToken();

                String nonceStr = (String) idToken.getJWTClaimsSet().getClaim("nonce");
                if (nonce != null) {
                    if (nonceStr == null || !nonce.getValue().equals(nonceStr)) {
                        throw new OpenIDConnectException("Invalid NONCE : " + nonceStr);
                    }
                }

                SignedJWT signedIdToken = (SignedJWT) idToken;

                // Verify signature
                signedIdToken.verify(verifier);
                claims = signedIdToken.getJWTClaimsSet();

                return successResponse;

            }

        } catch (Exception e) {
            throw new OpenIDConnectException(e.getMessage(), e);
        }
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
