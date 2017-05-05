package org.atricore.idbus.capabilities.csca.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.csca.producers.CscaNegotiationProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

public class CscaNegotiationEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {
    public CscaNegotiationEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new CscaNegotiationProducer( this );
    }
}