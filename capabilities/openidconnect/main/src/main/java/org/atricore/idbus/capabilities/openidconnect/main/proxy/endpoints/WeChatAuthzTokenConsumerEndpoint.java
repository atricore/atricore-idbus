package org.atricore.idbus.capabilities.openidconnect.main.proxy.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.WeChatAuthzConsumerTokenProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * Created by sgonzalez.
 */
public class WeChatAuthzTokenConsumerEndpoint  extends AbstractCamelEndpoint<CamelMediationExchange> {

    public WeChatAuthzTokenConsumerEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new WeChatAuthzConsumerTokenProducer( this );
    }
}