package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

/**
 *
 */
public class SSOSingleLogoutProducer extends AbstractOpenIDProducer {

    private static final Log logger = LogFactory.getLog(SSOSingleSignOnProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();

    public SSOSingleLogoutProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

    }
}