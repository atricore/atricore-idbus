package org.atricore.idbus.capabilities.oauth2.main.token.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.oauth2.main.token.producers.PasswordlessLinkProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

public class PasswordlessLinkEndpoint <E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public PasswordlessLinkEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new PasswordlessLinkProducer( this );
    }
}