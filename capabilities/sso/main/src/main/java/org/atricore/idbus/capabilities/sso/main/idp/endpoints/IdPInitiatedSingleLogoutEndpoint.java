package org.atricore.idbus.capabilities.sso.main.idp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.idp.producers.IdPInitiatedSingleLogoutProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 */
public class IdPInitiatedSingleLogoutEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public IdPInitiatedSingleLogoutEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new IdPInitiatedSingleLogoutProducer( this );
    }
}
