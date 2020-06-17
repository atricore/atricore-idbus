package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import net.minidev.json.JSONObject;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.rp.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URI;

/**
 * Since JOSSO is SAML native.  We create proxies for all other protocols.
 *
 * OIDC has a specific proxy for each relaying party, but the Token emissions in back channel are actually
 * running in the SSO/SAML IDP Provider.  Since we need the token as part of the RP Proxy state, we create a proxy
 * to call the actual token endpoint!
 *
 * @see org.atricore.idbus.capabilities.openidconnect.main.op.producers.TokenProducer
 *
 */
public class TokenProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public TokenProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        if (handleOptionsRequest(exchange)) { return; }

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();



        TokenRequest tokenRequest = (TokenRequest) in.getMessage().getContent();
        MediationState state = in.getMessage().getState();

        // Forward Token request to destination endpoint

        // Resolve IDP TOKEN endpoint, it supports multiple IDPs configured!
        RPAuthnContext authnCtx =
                (RPAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);
        if (authnCtx == null) {
            authnCtx = new RPAuthnContext();
        }


        EndpointDescriptor tokenEndpoint = lookupTokenEndpoint();

        // Use localhost actually!
        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();
        String targetBaseUrl = mediator.getKernelConfigCtx().getProperty("binding.http.localTargetBaseUrl", "http://localhost:8081");

        // Build token URI
        URI tokenUri = new URI(tokenEndpoint.getLocation());
        String internalTokenEndpoint = targetBaseUrl + tokenUri.getPath();

        // Create a new TOKEN request w/new IDP TOKEN ENDPOINT
        TokenRequest proxyTokenRequest = null;

        if (tokenRequest.getClientAuthentication() != null)
            proxyTokenRequest = new TokenRequest(
                    new URI(internalTokenEndpoint),
                    tokenRequest.getClientAuthentication(),
                    tokenRequest.getAuthorizationGrant(),
                    tokenRequest.getScope());
        else if (tokenRequest.getExistingGrant() != null)
            proxyTokenRequest = new TokenRequest(
                    new URI(internalTokenEndpoint),
                    tokenRequest.getClientID(),
                    tokenRequest.getAuthorizationGrant(),
                    tokenRequest.getScope(),
                    tokenRequest.getResources(),
                    tokenRequest.getExistingGrant(),
                    tokenRequest.getCustomParameters());
        else
            proxyTokenRequest = new TokenRequest(
                    new URI(internalTokenEndpoint),
                    tokenRequest.getClientID(),
                    tokenRequest.getAuthorizationGrant(),
                    tokenRequest.getScope());

        // Send request/process response
        // TODO : Eventually use mediation engine IdentityMediator mediator = channel.getIdentityMediator().sendMessage();
        HTTPResponse proxyResponse = proxyTokenRequest.toHTTPRequest().send();
        JSONObject jsonObject = proxyResponse.getContentAsJSONObject();

        if (proxyResponse.getStatusCode() == HTTPResponse.SC_OK) {
            OIDCTokenResponse proxyTokenResponse = OIDCTokenResponse.parse(jsonObject);
            OIDCTokenResponse at = proxyTokenResponse.toSuccessResponse();
            OIDCTokens tokens = at.getTokens().toOIDCTokens();

            JWT idToken = tokens.getIDToken();
            authnCtx.setIdToken(idToken);
            authnCtx.setAccessToken(tokens.getAccessToken());
            authnCtx.setRefreshToken(tokens.getRefreshToken());

            state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_REFRESH_TOKEN_KEY, tokens.getRefreshToken().getValue());
            state.getLocalState().addAlternativeId(OpenIDConnectConstants.SEC_CTX_ACCESS_TOKEN_KEY, tokens.getAccessToken().getValue());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    proxyTokenResponse,
                    "AccessTokenResponse",
                    "application/json",
                    null, // TODO
                    state));
        } else {
            TokenErrorResponse err = TokenErrorResponse.parse(jsonObject);
            authnCtx.setAccessToken(null);
            authnCtx.setRefreshToken(null);
            authnCtx.setIdToken(null);
            ErrorObject error = err.getErrorObject();

            if (logger.isDebugEnabled())
                logger.debug("Error obtaining Token : " + error.getCode() + ". " + error.getDescription());

            out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                    err,
                    "TokenErrorResponse",
                    "application/json",
                    null, // TODO
                    state));
        }

        // Store context
        state.setLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY, authnCtx);

        exchange.setOut(out);
    }

}
