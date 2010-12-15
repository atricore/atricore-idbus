package org.atricore.idbus.capabilities.spmlr2.main.psp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;
import org.atricore.idbus.capabilities.spmlr2.main.psp.producers.PSPProducer;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class PSPEndpoint<T> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public PSPEndpoint(String endpointURI, Component component, Map parameters) throws Exception {
        super(endpointURI, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer() throws Exception {
        return new PSPProducer(this);  
    }
}
