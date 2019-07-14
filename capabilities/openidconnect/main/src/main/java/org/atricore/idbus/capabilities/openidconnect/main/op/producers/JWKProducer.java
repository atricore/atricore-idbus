package org.atricore.idbus.capabilities.openidconnect.main.op.producers;

import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

public class JWKProducer extends AbstractOpenIDProducer {

    private static Log logger = LogFactory.getLog(JWKProducer.class);

    private static final UUIDGenerator uuidGenerator = new UUIDGenerator();


    public JWKProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange exchange) throws Exception {

        // TODO : Return signin/encryption keys from medatior stores as JWKSet
    }
}
