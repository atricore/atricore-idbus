package org.atricore.idbus.capabilities.preauthn.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.preauthn.ClaimColletor;
import org.atricore.idbus.capabilities.preauthn.PreAuthnException;
import org.atricore.idbus.capabilities.preauthn.PreAuthnMediator;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsRequest;
import org.atricore.idbus.capabilities.sso.main.claims.SSOCredentialClaimsResponse;
import org.atricore.idbus.capabilities.sso.support.binding.SSOBinding;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.Channel;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimChannel;
import org.atricore.idbus.kernel.main.mediation.claim.ClaimSet;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;
import org.atricore.idbus.kernel.main.mediation.provider.FederatedProvider;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public class PreAuthnProducer extends AbstractCamelProducer<CamelMediationExchange> {

    private static final Log logger = LogFactory.getLog(PreAuthnProducer.class);

    private UUIDGenerator uuidGenerator = new UUIDGenerator();

    public PreAuthnProducer(Endpoint endpoint) {
        super(endpoint);
    }

    /**
     * Receive a claims request and:
     *
     * 1. extract the custom token from a header or request parameter
     *
     * OR
     *
     * 2. redirect the user to another resource to get the token
     * 3. process the outcome of the redirection (POST/REDIR bindings getting all HTTP info)
     *
     * @param exchange
     * @throws Exception
     */
    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        SSOCredentialClaimsRequest request = (SSOCredentialClaimsRequest) in.getMessage().getContent();

        if (logger.isDebugEnabled())
            logger.debug("doProcess() - Received Pre-Authn claims request = " + request.getId());

        // TODO : Validate claims request

        PreAuthnMediator mediator = (PreAuthnMediator) channel.getIdentityMediator();

        ClaimColletor collector = mediator.getCollector();

        if (collector != null) {

            Channel issuer = request.getIssuerChannel();
            SSOBinding binding = SSOBinding.SSO_ARTIFACT;

            ClaimSet claims = collector.collect(getFederatedProvider().getName(), in);

            SSOCredentialClaimsResponse response = new SSOCredentialClaimsResponse(uuidGenerator.generateId(),
                    issuer, request.getId(), claims, request.getRelayState());

            // Resolve response location endpoint
            IdentityMediationEndpoint claimsProcessingEndpoint = null;

            // Look for an endpoint to send the response

            for (IdentityMediationEndpoint endpoint : issuer.getEndpoints()) {
                if (endpoint.getType().equals(request.getIssuerEndpoint().getType()) &&
                        endpoint.getBinding().equals(binding.getValue())) {
                    claimsProcessingEndpoint = endpoint;
                    break;
                }
            }

            if (claimsProcessingEndpoint == null) {
                throw new PreAuthnException("No endpoint supporting " + binding + " of type " +
                        request.getIssuerEndpoint().getType() + " found in channel " + request.getIssuerChannel().getName());
            }
            EndpointDescriptor ed = mediator.resolveEndpoint(request.getIssuerChannel(), claimsProcessingEndpoint);

            out.setMessage(new MediationMessageImpl(response.getId(),
                    response,
                    "ClaimsResponse",
                    null,
                    ed,
                    in.getMessage().getState()));

            exchange.setOut(out);

            return;

        } else if (mediator.getPreAuthnServiceURL() != null) {
            // TODO : Redirect to external resource (binding ?!)
            throw new UnsupportedOperationException("PreAuthnServiceURL not implemented yet!");
        }

        throw new IdentityMediationException("Unsupported pre-authentication configuration (no collector or external service");

    }

    protected FederatedProvider getFederatedProvider() {
        if (channel instanceof ClaimChannel) {
            ClaimChannel cc = (ClaimChannel) channel;
            return cc.getFederatedProvider();
        }

        return null;
    }
}
