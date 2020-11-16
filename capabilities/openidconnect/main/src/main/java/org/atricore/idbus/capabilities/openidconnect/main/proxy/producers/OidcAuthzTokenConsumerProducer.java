package org.atricore.idbus.capabilities.openidconnect.main.proxy.producers;

import org.atricore.idbus.capabilities.openidconnect.main.common.producers.OpenIDConnectProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

public class OidcAuthzTokenConsumerProducer extends OpenIDConnectProducer {

    public OidcAuthzTokenConsumerProducer(AbstractCamelEndpoint<CamelMediationExchange> endpoint) {
        super(endpoint);
    }

    @Override
    protected void doProcess(CamelMediationExchange e) throws Exception {
        // TODO : !

        e.getIn();
    }


}
