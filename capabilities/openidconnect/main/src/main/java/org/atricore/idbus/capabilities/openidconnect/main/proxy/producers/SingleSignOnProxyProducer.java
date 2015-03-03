package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.binding.OpenIDConnectBinding;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectException;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.OpenIDConnectProducer;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.OpenIDConnectProxyMediator;
import org.atricore.idbus.common.sso._1_0.protocol.SPInitiatedAuthnRequestType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptorImpl;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.Collections;

/**
 * Created by sgonzalez on 3/11/14.
 */
public class SingleSignOnProxyProducer extends OpenIDConnectProducer {

    private static final Log logger = LogFactory.getLog(SingleSignOnProxyProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SingleSignOnProxyProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        // Process an authn request,
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        SPInitiatedAuthnRequestType authnRequest = (SPInitiatedAuthnRequestType) in.getMessage().getContent();

        doProcessSPInitiatedSSO(exchange, authnRequest);
    }


    /**
     * This procedure will process an authn request.
     * <p/>
     * <p/>
     * If we already stablished identity for the 'presenter' (user) of the request, we'll generate
     * an assertion using the authn statement stored in session as security token.
     * The assertion will be sent to the SP in a new Response.
     * <p/>
     * <p/>
     * If we don't have user identity yet, we have to decide if we're handling the request or we are proxying it to a
     * different IDP.
     * If we handle the request, we'll search for a claims endpoint and start collecting claims.  If no claims endpoint
     * are available, we're sending a status error response. (we could look for a different IDP here!)
     */
    protected void doProcessSPInitiatedSSO(CamelMediationExchange exchange, SPInitiatedAuthnRequestType authnRequest)
            throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        // redirect to the authorization flow
        String relayState = uuidGenerator.generateId();

        AuthorizationCodeRequestUrl authorizationUrl =
                new AuthorizationCodeRequestUrl(mediator.getAuthzTokenServiceLocation(), mediator.getClientId());

        // Keep track of state
        authorizationUrl.setState(relayState);

        // Set google apps domain, if available
        if (mediator.getGoogleAppsDomain() != null) {
            if (logger.isDebugEnabled())
                logger.debug("Setting hd URL parameter to ["+mediator.getGoogleAppsDomain()+"]");
            authorizationUrl.set("hd", mediator.getGoogleAppsDomain());
        }

        // Request the proper scopes, this varies from IdP to IdP:

        String scopes = mediator.getScopes();


        if (logger.isDebugEnabled())
            logger.debug("Setting scopes URL parameter to [" + scopes + "]");

        authorizationUrl.setScopes(Collections.singleton(scopes));

        // Keep track of state
        mediationState.setLocalVariable("urn:OPENID-CONNECT:1.0:relayState", relayState);
        mediationState.setLocalVariable("urn:OPENID-CONNECT:1.0:authnRequest", authnRequest);

        // Resolve authz token service endpoint
        EndpointDescriptor responseLocation = resolveAuthzTokenServiceEndpoint();

        if (responseLocation == null)
            throw new OpenIDConnectException("Cannot resolve local Authorization Code token service endpoint");

        if (logger.isDebugEnabled())
            logger.debug("AuthzTokenConsumer: " + responseLocation.getLocation());

        authorizationUrl.setRedirectUri(responseLocation.getLocation());

        // Create Mediation message with redirect
        EndpointDescriptor ed = new EndpointDescriptorImpl("authzCodeService",
                OpenIDConnectConstants.AuthzCodeProviderService_QNAME.getLocalPart(),
                OpenIDConnectBinding.OPENIDCONNECT_AUTHZ.getValue(),
                authorizationUrl.build(),
                null);

        out.setMessage(new MediationMessageImpl(relayState,
                authorizationUrl,
                "OpenIDAuthenticationRequest",
                relayState,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }


    protected EndpointDescriptor resolveAuthzTokenServiceEndpoint() {

        String googleSvc = OpenIDConnectConstants.GoogleAuthzTokenConsumerService_QNAME.toString();
        String fbSvc = OpenIDConnectConstants.FacebookAuthzTokenConsumerService_QNAME.toString();
        String binding = OpenIDConnectBinding.OPENIDCONNECT_AUTHZ.toString();

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getType().equals(googleSvc) ||
                    endpoint.getType().equals(fbSvc) ||
                    endpoint.getType().endsWith("AuthzTokenConsumerService")) { // Kind of a hack

                if (endpoint.getBinding().equals(binding))
                    return new EndpointDescriptorImpl(channel.getLocation(), endpoint);
            }
        }

        logger.warn("No endpoint found for service/binding " +
                        "<IdPType>AuthzTokenConsumerService/" +
                binding);

        return null;
    }

}
