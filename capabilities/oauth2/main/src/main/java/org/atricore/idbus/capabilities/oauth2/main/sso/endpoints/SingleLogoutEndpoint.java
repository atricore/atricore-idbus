package org.atricore.idbus.capabilities.oauth2.main.sso.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.oauth2.main.sso.producers.SingleLogoutProducer;
import org.atricore.idbus.capabilities.oauth2.main.sso.producers.SingleSignOnProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * Created by sgonzalez.
 */
public class SingleLogoutEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SingleLogoutEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new SingleLogoutProducer( this );
    }
}