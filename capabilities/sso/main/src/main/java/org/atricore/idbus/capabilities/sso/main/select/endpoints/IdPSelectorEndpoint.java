package org.atricore.idbus.capabilities.sso.main.select.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.select.producers.IdPSelectorProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 *
 */
public class IdPSelectorEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public IdPSelectorEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new IdPSelectorProducer( this );
    }
}
