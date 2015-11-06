package org.atricore.idbus.capabilities.sso.main.sp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.sp.producers.SPInitiatedSingleLogoutProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * Created by sgonzalez on 7/28/14.
 */
public class SPInitiatedSingleLogoutServiceProxyEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SPInitiatedSingleLogoutServiceProxyEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new SPInitiatedSingleLogoutProducer( this );
    }
}
