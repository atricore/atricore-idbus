package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 *
 */
public class TokenProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(AuthorizationProducer.class);

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

        validateRequest(exchange, tokenRequest);

        String grantType = null; // TODO : tokenRequest.getGrantType();
        // TODO : Support other grant types

        EndpointDescriptor responseLocation = null;




        // Issue Access Token

        // Issue Refresh Token (optional)

        // Issue ID Token

        TokenResponse tokenResponse = buildAccessTokenResponse();

        // Send response back (this is a back-channel request)
        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                tokenResponse,
                "AccessTokenResponse",
                null,
                responseLocation,
                in.getMessage().getState()));

        exchange.setOut(out);

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
        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();

        // Client ID
        ClientID expectedClientId = tokenRequest.getClientID();
        if (logger.isDebugEnabled())
            logger.debug("Processing TokenRequest for " + expectedClientId.getValue());

        OIDCClientInformation clientMD  = mediator.getClients().get(expectedClientId.getValue());

        // Verify that the Client support the Authorization Grant
        AuthorizationGrant grant = tokenRequest.getAuthorizationGrant();
        if (clientMD.getOIDCMetadata().getGrantTypes().contains(grant.getType())) {
            throw new OpenIDConnectException("Invalid Authorization Grant ["+tokenRequest.getAuthorizationGrant()+"] for Client " + expectedClientId.getValue());
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

            // This grant type is stateless, no authorization code is required
            JWTBearerGrant jwtBearerGrant = (JWTBearerGrant) grant;

            JWT assertion = jwtBearerGrant.getJWTAssertion();

            // TODO : Check JWT Bearer options (i.e. is ecryption/signature required)
            if (assertion instanceof EncryptedJWT) {

            }

            // This should be JWE



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

    protected TokenResponse buildAccessTokenResponse() {
        return null;
    }

    protected void validateRequest(TokenRequest tokenRequest, OpenIDConnectAuthnContext authnCtx) throws OpenIDConnectException {
        // TODO : Validate received code w/authnCtx authz code

        // TODO : Validate grant_type

        // TODO : Validate request_uri (wiht the one requested, not configured!)

    }
}
