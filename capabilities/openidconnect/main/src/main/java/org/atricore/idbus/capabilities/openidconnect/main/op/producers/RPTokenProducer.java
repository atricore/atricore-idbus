package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectAuthnContext;
import org.atricore.idbus.capabilities.openidconnect.main.op.OpenIDConnectBPMediator;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.federation.metadata.CircleOfTrustMemberDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.channel.FederationChannel;
import org.atricore.idbus.kernel.main.mediation.channel.SPChannel;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.mediation.provider.FederationService;
import org.atricore.idbus.kernel.main.mediation.provider.IdentityProvider;
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

    private static final Log logger = LogFactory.getLog(TokenProducer.class);

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

        // Create a new TOKEN request w/new IDP TOKEN ENDPOINT
        TokenRequest proxyTokenRequest = new TokenRequest(new URI(tokenEndpoint.getLocation()),
                tokenRequest.getClientAuthentication(), tokenRequest.getAuthorizationGrant(), tokenRequest.getScope());

        // Send request/process response (TODO : Eventually use mediation engine)
        HTTPResponse proxyResponse = proxyTokenRequest.toHTTPRequest().send();

        TokenResponse proxyTokenResponse = TokenResponse.parse(proxyResponse);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                proxyTokenResponse,
                "AccessTokenResponse",
                "application/json",
                null, // TODO
                in.getMessage().getState()));

        exchange.setOut(out);
    }

    protected ServiceProvider lookupSPProxy() {
        OpenIDConnectBPMediator mediator = (OpenIDConnectBPMediator) channel.getIdentityMediator();
        String spAlias = mediator.getSpAlias();
        for (FederatedProvider provider : getFederatedProvider().getCircleOfTrust().getProviders()) {
            if (provider instanceof ServiceProvider) {

                ServiceProvider sp = (ServiceProvider)provider;
                for (CircleOfTrustMemberDescriptor m : sp.getMembers()) {
                    if (m.getAlias().equals(spAlias)) {
                        if (logger.isDebugEnabled())
                            logger.debug("Found Service Provider " + provider.getName() + " for alias " + spAlias);
                        return (ServiceProvider) provider;
                    }
                }
            }
        }

        logger.error("No SP Proxy found for alias " + spAlias);

        return null;

    }

    protected EndpointDescriptor lookupTokenEndpoint(OpenIDConnectAuthnContext authnCtx) throws IdentityMediationException {

        // Get SP Proxy -> IDP Channel -> IDP -> OIDC Service -> Channel -> Token Endpoint
        ServiceProvider spProxy = lookupSPProxy();
        if (spProxy == null) {
            return null;
        }

        // Now, we need to identify the selected IDP
        SPChannel spChannel = lookupSPChannel(spProxy, authnCtx.getIdpAlias());
        if (spChannel == null)
            return null;

        IdentityMediationEndpoint tokenEndpoint = null;
        for (IdentityMediationEndpoint endpoint : spChannel.getEndpoints()) {
            if (endpoint.getType().equals(OpenIDConnectService.TokenService.toString()) &&
                    endpoint.getBinding().equals(OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue())) {

                // This is the ED!
                tokenEndpoint = endpoint;

                if (logger.isDebugEnabled())
                    logger.debug("Using TOKEN RESTFUL endpoint [" + tokenEndpoint.getLocation() + "]");
                break;
            }
        }

        if (tokenEndpoint != null)
            return spChannel.getIdentityMediator().resolveEndpoint(spChannel, tokenEndpoint);

        logger.error("No Token Endpoint [" +
                OpenIDConnectService.TokenService.toString() + "/" +
                OpenIDConnectBinding.OPENID_PROVIDER_TOKEN_RESTFUL.getValue()+"] in channel " + spChannel.getName());

        return null;
    }

    protected SPChannel lookupSPChannel(ServiceProvider spProxy, String idpAlias) {
        SPChannel spChannel = null;
        IdentityProvider idp = null;
        for (FederatedProvider prov : spProxy.getChannel().getTrustedProviders()) {
            if (prov instanceof IdentityProvider) {
                idp = (IdentityProvider) prov;

                FederationService oidcService = null;
                if (idp.getDefaultFederationService().getServiceType().equals("urn:org:atricore:idbus:OIDC:1.0")) {
                    oidcService = idp.getDefaultFederationService();
                } else {
                    for (FederationService svc : idp.getFederationServices()) {
                        if (svc.getServiceType().equalsIgnoreCase("urn:org:atricore:idbus:OIDC:1.0")) {
                            oidcService = svc;
                            break;
                        }
                    }
                }

                if (oidcService == null) {
                    logger.debug("IDP " + idp.getName() + " does not have OIDC service, make sure to enable OIDC.");
                    continue;
                }

                // Use default channel from OIDC service
                spChannel = (SPChannel) oidcService.getChannel();
            }
        }

        if (spChannel == null) {
            logger.error("No SP channel found for alias " + idpAlias);
        }

        return spChannel;
    }
}
