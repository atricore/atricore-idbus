package org.atricore.idbus.capabilities.openidconnect.main.rp.producer;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.producers.AbstractOpenIDProducer;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationMessage;

public class CheckSessionIFrameProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(UserInfoProducer.class);

    public CheckSessionIFrameProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        CamelMediationMessage in = (CamelMediationMessage) exchange.getIn();

        in.getMessage().getContent();
    }
}
