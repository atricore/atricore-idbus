package org.atricore.idbus.capabilities.oauth2.main.authorization.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.oauth2.main.authorization.producers.AuthorizationProducer;
import org.atricore.idbus.capabilities.oauth2.main.sso.producers.AssertionConsumerProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AuthorizationEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public AuthorizationEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new AuthorizationProducer( this );
    }
}

