package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.IdentityMediator;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.ServiceProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.net.URI;

/**
 * Since JOSSO is SAML native.  We create proxies for all other protocols.
 *
 * OIDC has a specific proxy for each relaying party, but the Token emissions in back channel are actually
 * running in the SSO/SAML IDP Provider.  Since we need the token as part of the RP Proxy state, we create a proxy
 * to call the actual token endpoint!
 *
 * @see TokenProducer
 *
 */
public class RPTokenProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(RPTokenProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public RPTokenProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        TokenRequest tokenRequest = (TokenRequest) in.getMessage().getContent();
        MediationState state = in.getMessage().getState();

        // Forward Token request to destination endpoint

        // Resolve IDP TOKEN endpoint, it supports multiple IDPs configured!
        OpenIDConnectAuthnContext authnCtx =
                (OpenIDConnectAuthnContext) state.getLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY);


        EndpointDescriptor tokenEndpoint = lookupTokenEndpoint(authnCtx);

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

        OIDCTokenResponse proxyTokenResponse = OIDCTokenResponse.parse(proxyResponse);

        if (proxyTokenResponse.indicatesSuccess()) {
            OIDCTokenResponse at = proxyTokenResponse.toSuccessResponse();
            OIDCTokens tokens = at.getTokens().toOIDCTokens();

        } else {
            TokenErrorResponse err = proxyTokenResponse.toErrorResponse();
            authnCtx.setAccessToken(null);
            authnCtx.setRefreshToken(null);
            authnCtx.setIdToken(null);
            ErrorObject error = err.getErrorObject();

            if (logger.isDebugEnabled())
                logger.debug("Error obtaining Token : " + error.getCode() + ". " + error.getDescription());

        }

        // Store context
        state.setLocalVariable(OpenIDConnectConstants.AUTHN_CTX_KEY, authnCtx);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                proxyTokenResponse,
                "AccessTokenResponse",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);
    }

}
