package org.atricore.idbus.capabilities.sso.main.idp.producers;

import oasis.names.tc.saml._2_0.protocol.ResponseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.capabilities.sso.main.idp.SSOIDPMediator;
import org.atricore.idbus.capabilities.sso.support.SSOConstants;
import org.atricore.idbus.capabilities.sso.support.core.signature.SamlR2Signer;
import org.atricore.idbus.common.sso._1_0.protocol.SPAuthnResponseType;
import org.atricore.idbus.common.sso._1_0.protocol.SSOResponseType;
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
public class IdPSelectorCallbackProducer extends SSOProducer {

    private static final Log logger = LogFactory.getLog(IdPSelectorCallbackProducer.class);

    private static UUIDGenerator uuidGenerator = new UUIDGenerator();

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

        Object ssoResponse = state.getLocalVariable(SSOConstants.SSO_RESPONSE_VAR_TMP);
        EndpointDescriptor destination = (EndpointDescriptor) state.getLocalVariable(SSOConstants.SSO_RESPONSE_ENDPOINT_VAR_TMP);
        String relayState = (String) state.getLocalVariable(SSOConstants.SSO_RESPONSE_RELAYSTATE_VAR_TMP);
        String type = (String) state.getLocalVariable(SSOConstants.SSO_RESPONSE_TYPE_VAR_TMP);
        SamlR2Signer signer = (SamlR2Signer) state.getLocalVariable(SSOConstants.SSO_RESPONSE_SIGNER_VAR_TMP);

        state.setAttribute("SAMLR2Signer", signer);

        if (logger.isDebugEnabled()) {
            if (ssoResponse instanceof ResponseType) {
                ResponseType r = (ResponseType) ssoResponse;
                logger.debug("Relaying Response " + r.getID() + " [issuer:" + r.getIssuer().getValue() + "]");
            } else if (ssoResponse instanceof oasis.names.tc.saml._1_0.protocol.ResponseType) {
                oasis.names.tc.saml._1_0.protocol.ResponseType r = (oasis.names.tc.saml._1_0.protocol.ResponseType) ssoResponse;
                logger.debug("Relaying Response " + r.getResponseID());
            } else {
                logger.debug("Relaying Response " + ssoResponse);
            }
        }

        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_VAR_TMP);
        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_ENDPOINT_VAR_TMP);
        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_RELAYSTATE_VAR_TMP);
        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_TYPE_VAR_TMP);
        state.removeLocalVariable(SSOConstants.SSO_RESPONSE_SIGNER_VAR_TMP);


        // ---------------------------------------------------
        // Send Response
        // ---------------------------------------------------

        out.setMessage(new MediationMessageImpl(uuidGenerator.generateId(),
                ssoResponse, type != null ? type : "Response", relayState, destination, in.getMessage().getState()));

        exchange.setOut(out);

    }
}
