package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.SsoPayloadResolutionBinding;
import org.atricore.idbus.capabilities.sso.main.common.producers.SSOProducer;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationException;
import org.atricore.idbus.kernel.main.mediation.MediationMessageImpl;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public class PayloadResolutionProducer  extends SSOProducer {

    private static final Log logger = LogFactory.getLog(PayloadResolutionProducer.class);

    public PayloadResolutionProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();
        CamelMediationMessage out = (CamelMediationMessage) exchange.getOut();

        MediationState state = in.getMessage().getState();

        String uuid = state.getTransientVariable(SsoPayloadResolutionBinding.SSO_PAYLOAD);
        if (uuid == null) {
            throw new IdentityMediationException("No payload uuid found");
        }

        String payload = (String) state.getLocalVariable(uuid);
        state.removeLocalVariable(uuid);

        if (payload == null) {
            throw new IdentityMediationException("No payload found for : " + uuid);
        }

        out.setMessage(new MediationMessageImpl(UUIDGenerator.generateJDKId(),
                null,
                payload,
                "application/html",
                null,
                null,
                in.getMessage().getState()));

        exchange.setOut(out);

    }
}
