package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectConstants;
import org.atricore.idbus.capabilities.openidconnect.main.common.binding.OpenIDConnectBinding;
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

import java.net.URI;

/**
 * This producer receives an SP initiated request and issues an authorization token request to the external
 * OIDC identity provider.
 */
public class SPInitiatedProducer extends OpenIDConnectProducer {

    private static final Log logger = LogFactory.getLog(SingleSignOnProxyProducer.class);

    protected UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SPInitiatedProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) throws Exception {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        MediationState mediationState = in.getMessage().getState();
        OpenIDConnectProxyMediator mediator = (OpenIDConnectProxyMediator) channel.getIdentityMediator();

        // Process an authn request,
        SPInitiatedAuthnRequestType ssoAuthnRequest = (SPInitiatedAuthnRequestType) in.getMessage().getContent();
        // TODO : ssoAuthnRequest

        // This is the OpenID Connect Identity Provider ID (in JOSSO is the base URI for the OP services)
        URI authnEndpoint = new URI(mediator.getAccessTokenServiceLocation());

        // Redirect URI.  Where to redirect after authentication

        // Resolve authz token service endpoint
        EndpointDescriptor responseLocation = resolveAuthnResponseEndpoint();

        URI redirectUri = new URI(responseLocation.getLocation());

        Scope scope = Scope.parse("openid ,profile ,email"); // TODO !

        ResponseType rt = new ResponseType();
        rt.add(ResponseType.Value.CODE);

        ClientID clientId = new ClientID(mediator.getClientId());
        State state = new State(mediationState.getLocalState().getId());
        Nonce nonce = new Nonce();

        ResponseMode rm = ResponseMode.QUERY;
        AuthenticationRequest authnRequest = new AuthenticationRequest.Builder(
                rt,
                scope,
                clientId,
                redirectUri).
                endpointURI(authnEndpoint).
                state(state).
                nonce(nonce).
                responseMode(rm).
                build();

        mediationState.setLocalVariable("urn:OPENID-CONNECT:1.0:ssoAuthnRequest", ssoAuthnRequest);
        mediationState.setLocalVariable("urn:OPENID-CONNECT:1.0:nonce", nonce);

        // Create Mediation message with redirect
        EndpointDescriptor ed = new EndpointDescriptorImpl("authzCodeService",
                OpenIDConnectConstants.AuthzCodeProviderService_QNAME.getLocalPart(),
                OpenIDConnectBinding.OPENID_PROXY_RELAYING_PARTY_AUTHZ_HTTP.getValue(),
                responseLocation.getLocation(),
                null);

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                authnRequest,
                "AuthenticationRequest",
                null,
                ed,
                in.getMessage().getState()));

        exchange.setOut(out);

    }

    protected EndpointDescriptor resolveAuthnResponseEndpoint() {

        String azureSvc =  OpenIDConnectConstants.AzureAuthzTokenConsumerService_QNAME.toString();
        String binding = OpenIDConnectBinding.OPENID_PROXY_RELAYING_PARTY_AUTHZ_HTTP.toString();

        for (IdentityMediationEndpoint endpoint : channel.getEndpoints()) {
            if (endpoint.getType().equals(azureSvc) ||
                    endpoint.getType().endsWith("AuthzTokenConsumerService")) { // TODO : Kind of a hack!!!!!

                if (endpoint.getBinding().equals(binding))
                    return new EndpointDescriptorImpl(channel.getLocation(), endpoint);
            }
        }

        logger.warn("No endpoint found for service/binding " + "<IdPType>AuthzTokenConsumerService/" + binding);
        return null;
    }

}
