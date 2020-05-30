package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import com.nimbusds.oauth2.sdk.id.State;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.capabilities.openidconnect.main.rp.RPAuthnContext;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public class OPStateProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(OPStateProducer.class);


    public OPStateProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {
        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState state = in.getMessage().getState();

        String sessionStateStr = "NA";
        RPAuthnContext authnContext = (RPAuthnContext) state.getLocalVariable(AUTHN_CTX_KEY);
        if (authnContext != null) {

            // TODO: Access session
            State sessionState = authnContext.getRPSessionState();
            if (sessionState != null) {
                sessionStateStr = sessionState.getValue();
            }
        }

        out.setMessage(new MediationMessageImpl(UUIDGenerator.generateJDKId(),
                null,
                sessionStateStr,
                "application/html",
                null,
                null,
                in.getMessage().getState()));

        exchange.setOut(out);

    }
}
