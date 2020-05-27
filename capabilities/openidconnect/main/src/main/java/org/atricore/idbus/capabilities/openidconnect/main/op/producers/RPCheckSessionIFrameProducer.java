package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

public class RPCheckSessionIFrameProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(RPUserInfoProducer.class);

    public RPCheckSessionIFrameProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        in.getMessage().getContent();
    }
}
