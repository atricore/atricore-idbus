package org.atricore.idbus.capabilities.spnego.endpoints;

import org.atricore.idbus.capabilities.spnego.producers.SpnegoNegotiationProducer;
import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 */
public class SpnegoNegotiationEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {
    public SpnegoNegotiationEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new SpnegoNegotiationProducer( this );
    }
}