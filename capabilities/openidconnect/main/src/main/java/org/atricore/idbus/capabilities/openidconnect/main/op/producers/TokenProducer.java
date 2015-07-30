package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.*;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectOPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectProviderException;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.binding.BindingChannel;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedLocalProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.text.*;
import java.util.Arrays;

/**
 *
 */
public class TokenProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public TokenProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        TokenRequest tokenRequest = (TokenRequest) in.getMessage().getContent();
        MediationState state = in.getMessage().getState();


        OpenIDConnectBinding binding = OpenIDConnectBinding.asEnum(endpoint.getBinding());
        EndpointDescriptor errorEndpoint = new EndpointDescriptorImpl("rp-error",
                OpenIDConnectService.TokenService.toString(),
                binding.getValue(), null, null);

        // If we have an authorization_code , the state MUST have the proper alternate key.

        AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();
        if (grant instanceof AuthorizationCodeGrant) {
            String authzCode = state.getLocalState().getAlternativeId("authorization_code");
            if (authzCode == null) {
                // TODO : Send error response
                TokenErrorResponse errorResponse = buildErrorResponse(OAuth2Error.INVALID_REQUEST);
                // Send response back (this is a back-channel request)
                out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                        errorResponse,
                        "AccessTokenResponse",
                        null,
                        errorEndpoint,
                        in.getMessage().getState()));

                exchange.setOut(out);
                return;
            }
        }

        validateRequest(exchange, tokenRequest);

        // Depending on the authorization grant, we perform different emissions

        if (grant.getType().equals(GrantType.JWT_BEARER)) {
            JWTBearerGrant jwtBearerGrant = (JWTBearerGrant) grant;

            JWT assertion = jwtBearerGrant.getJWTAssertion();
            ReadOnlyJWTClaimsSet claimsSet = assertion.getJWTClaimsSet();

            if (claimsSet.getClaim("cred") != null) {
                // Now, if the cred claim is available, and the grant is supported, use basic auth for the user.
            }



        }

        // TODO : Support other grant types

        // Issue Access Token

        // Issue Refresh Token (optional)

        // Issue ID Token

        TokenResponse tokenResponse = buildAccessTokenResponse();

        // Send response back (this is a back-channel request)
        /*
        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                tokenResponse,
                "AccessTokenResponse",
                null,
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);
        */

        throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "grant_type:" + grant.getType().getValue());

    }

    /**
     * This validates the received Token request.
     *
     * Verify authorization grant
     *
     * @param exchange
     * @param tokenRequest
     * @throws OpenIDConnectException
     */
    protected void validateRequest(CamelMediationExchange exchange, TokenRequest tokenRequest)
            throws OpenIDConnectException {

        // Authenticate Client:
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState state = in.getMessage().getState();
        OpenIDConnectOPMediator mediator = (OpenIDConnectOPMediator) channel.getIdentityMediator();

        // Client ID (also taken from parameter
        ClientID clientID = tokenRequest.getClientAuthentication() != null ?
                tokenRequest.getClientAuthentication().getClientID() : tokenRequest.getClientID();

        if (clientID == null)
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_REQUEST, "client_id:n/a");

        if (logger.isDebugEnabled())
            logger.debug("Processing TokenRequest for " + clientID.getValue());

        OIDCClientInformation clientMD  = resolveClient(clientID);
        if (clientMD == null) {
            throw new OpenIDConnectProviderException(OAuth2Error.UNAUTHORIZED_CLIENT, "client_id:" + clientID.getValue());
        }

        if (logger.isDebugEnabled())
            logger.debug("Processing TokenRequest for " + clientID.getValue());

        ClientID expectedClientId = clientMD.getID();

        // Verify that the Client support the Authorization Grant
        AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();
        if (grant == null)
            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "grant_type:n/a");

        if (clientMD.getOIDCMetadata().getGrantTypes().contains(grant.getType())) {
            if (logger.isDebugEnabled())
                logger.debug("Invalid Authorization Grant ["+tokenRequest.getAuthorizationGrant()+"] for Client " + expectedClientId.getValue());

            throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, "grant_type:" + grant.getType().getValue());
        }

        // Make Grant specific validations:
        if (grant.getType().equals(GrantType.AUTHORIZATION_CODE)) {

            if (logger.isTraceEnabled())
                logger.trace("Validating Authorization Code Grant [" + expectedClientId.getValue() + "]");

            // This grant requires a valid state, mapped to a front-channel state
            OpenIDConnectAuthnContext authnCtx = (OpenIDConnectAuthnContext)
                    state.getLocalVariable("urn:org:atricore:idbus:capabilities:openidconnect:authnCtx");

            AuthorizationCodeGrant authzCodeGrant = (AuthorizationCodeGrant) grant;
            AuthorizationCode code = authzCodeGrant.getAuthorizationCode();

            AuthorizationCode expectedCode = authnCtx.getAuthorizationCode();
            // Clear code, so it cannot be reused
            authnCtx.setAuthorizationCode(null);

            if (code == null) {
                // TODO : Send error response
            } else if (expectedCode == null) {
                // TODO : Send error, invalid code
            } else if (!code.equals(expectedCode)) {
                // TODO : Send error, invalid code
            }

            // Validate Authorization Code expiration
            if (authnCtx.getAuthorizationCodeNotOnOrAfter() > 0 &&
                    authnCtx.getAuthorizationCodeNotOnOrAfter() < System.currentTimeMillis()) {
                // TODO : Send error, authorization code expired

            }


        } else if (grant.getType().equals(GrantType.JWT_BEARER)) {

            if (logger.isTraceEnabled())
                logger.trace("Validating JWT Bearer Grant [" + expectedClientId.getValue() + "]");

            try {
                // This grant type is stateless, no authorization code is required
                JWTBearerGrant jwtBearerGrant = (JWTBearerGrant) grant;
                EncryptedJWT assertion = (EncryptedJWT) jwtBearerGrant.getJWTAssertion();

                // TODO : Is this standard procedure ?!
                byte[] key = clientMD.getSecret().getValueBytes();
                MessageDigest sha = MessageDigest.getInstance("SHA-1");
                key = sha.digest(key);
                key = Arrays.copyOf(key, 32);
                SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

                // Decrypt TODO : Configure encryption (AES), etc
                JWEDecrypter decrypter = new DirectDecrypter(secretKey.getEncoded());
                assertion.decrypt(decrypter);
                ReadOnlyJWTClaimsSet claims = assertion.getJWTClaimsSet();

                // Verify Signature : TODO : Configure Verifier

            } catch (java.text.ParseException e) {
                logger.error(e.getMessage(), e);
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, e.getMessage());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new OpenIDConnectProviderException(OAuth2Error.INVALID_GRANT, e.getMessage());
            }


        } if (grant.getType().equals(GrantType.CLIENT_CREDENTIALS)) {

            if (logger.isTraceEnabled())
                logger.trace("Validating Client Credentials Grant [" + expectedClientId.getValue() + "]");

            Secret expectedSecret = clientMD.getSecret();

            Secret secret = null;
            ClientID clientId = null;

            ClientCredentialsGrant clientCredentialsGrant = (ClientCredentialsGrant) grant;
            ClientAuthentication clientAuthn = tokenRequest.getClientAuthentication();
            if (clientAuthn instanceof ClientSecretBasic) {
                ClientSecretBasic clientBasicAuthn = (ClientSecretBasic) clientAuthn;
                secret = clientBasicAuthn.getClientSecret();
                clientId = clientBasicAuthn.getClientID();
            } else if (clientAuthn instanceof ClientSecretPost) {
                ClientSecretPost clientPostAuthn = (ClientSecretPost) clientAuthn;
                secret = clientPostAuthn.getClientSecret();
                clientId = clientPostAuthn.getClientID();
            } else {
                // TODO : Send error
            }

            if (expectedSecret.expired()) {
                logger.warn("Client " + clientId.getValue() + " credentials have expired.");
                // TODO : Send error
            }

            if (clientId == null || !expectedClientId.equals(clientId)) {
                // TODO : Send error
            }

            if (secret == null || !expectedSecret.equals(secret)) {
                // TODO : Send error
            }

            // TODO : Send error response

        } else {
            // TODO : Send error response
        }


    }

    protected OIDCClientInformation resolveClient(ClientID clientID) {
        FederationChannel fChannel = (FederationChannel) channel;

        for (FederatedProvider p : fChannel.getTrustedProviders()) {
            // We are looking for a SAML 2 SP Proxy for the Relaying Party.
            if (p instanceof ServiceProvider) {
                ServiceProvider sp = (ServiceProvider) p;

                String bpRole = sp.getBindingChannel().getFederatedProvider().getRole();

                if (bpRole.equals(OpenIDConnectConstants.IDPSSODescriptor_QNAME.toString())) {
                    // This is a relaying party facing channel.
                    BindingChannel bc = sp.getBindingChannel();

                    OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) bc.getIdentityMediator();
                    OIDCClientInformation client = mediator.getClient();
                    if (client.getID().equals(clientID))
                        return client;

                }
            }
        }
        return null;

    }

    protected TokenErrorResponse buildErrorResponse(String code , String description) {
        return buildErrorResponse(new ErrorObject(code, description, HTTPResponse.SC_BAD_REQUEST));
    }


    protected TokenErrorResponse buildErrorResponse(String code , String description, int httpStatusCode) {
        return buildErrorResponse(new ErrorObject(code, description, httpStatusCode));
    }

    protected TokenErrorResponse buildErrorResponse(ErrorObject error) {
        TokenErrorResponse r = new TokenErrorResponse(error);

        return r;
    }

    protected TokenResponse buildAccessTokenResponse() {
        return null;
    }

    protected void validateRequest(TokenRequest tokenRequest, OpenIDConnectAuthnContext authnCtx) throws OpenIDConnectException {
        // TODO : Validate received code w/authnCtx authz code

        // TODO : Validate grant_type

        // TODO : Validate request_uri (wiht the one requested, not configured!)

    }
}
