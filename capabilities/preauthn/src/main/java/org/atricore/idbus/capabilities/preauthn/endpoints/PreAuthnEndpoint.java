package org.atricore.idbus.capabilities.preauthn.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.preauthn.producers.PreAuthnProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

public class PreAuthnEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {
    public PreAuthnEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new PreAuthnProducer( this );
    }
}