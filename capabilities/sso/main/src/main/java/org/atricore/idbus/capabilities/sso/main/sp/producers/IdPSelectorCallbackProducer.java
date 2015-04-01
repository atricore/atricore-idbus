package org.atricore.idbus.capabilities.sso.main.sp.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.support.SSOConstants;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
import org.atricore.idbus.kernel.main.federation.metadata.EndpointDescriptor;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

/**
 * Created by sgonzalez on 3/31/15.
 */
public class IdPSelectorCallbackProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog(IdPSelectorCallbackProducer.class);

    public IdPSelectorCallbackProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        doProcessSSOResponseType(exchange, (SSOResponseType) in.getMessage().getContent());
    }

    protected void doProcessSSOResponseType(CamelMediationExchange exchange, SSOResponseType response) throws Exception {

        // Incomming message
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        // Mediation state
        MediationState state = in.getMessage().getState();

        SPAuthnResponseType ssoResponse = (SPAuthnResponseType) state.getLocalVariable(SSOConstants.SSO_RESPONSE_VAR_TMP);
        EndpointDescriptor destination = (EndpointDescriptor) state.getLocalVariable(SSOConstants.SSO_RESPONSE_ENDPOINT_VAR_TMP);

        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_VAR_TMP);
        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_ENDPOINT_VAR_TMP);

        // ---------------------------------------------------
        // Send SPAuthnResponse
        // ---------------------------------------------------

        out.setMessage(new MediationMessageImpl(ssoResponse.getID(),
                ssoResponse, "SPAuthnResponse", null, destination, in.getMessage().getState()));

        exchange.setOut(out);

    }
}
