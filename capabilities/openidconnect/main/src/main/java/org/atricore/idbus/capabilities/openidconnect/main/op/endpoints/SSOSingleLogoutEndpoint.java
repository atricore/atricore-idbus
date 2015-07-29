package org.atricore.idbus.capabilities.openidconnect.main.op.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.openidconnect.main.op.producers.SSOSingleLogoutProducer;
import org.atricore.idbus.capabilities.openidconnect.main.op.producers.TokenProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 */
public class SSOSingleLogoutEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SSOSingleLogoutEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new SSOSingleLogoutProducer( this );
    }
}
