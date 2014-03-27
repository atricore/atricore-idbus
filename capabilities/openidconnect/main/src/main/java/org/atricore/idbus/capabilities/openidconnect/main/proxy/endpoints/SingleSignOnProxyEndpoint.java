package org.atricore.idbus.capabilities.openidconnect.main.proxy.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.producers.SingleSignOnProxyProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * Created by sgonzalez on 3/11/14.
 */
public class SingleSignOnProxyEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SingleSignOnProxyEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new SingleSignOnProxyProducer( this );
    }
}
