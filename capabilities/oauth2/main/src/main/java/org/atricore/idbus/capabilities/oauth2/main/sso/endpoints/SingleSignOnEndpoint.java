package org.atricore.idbus.capabilities.oauth2.main.sso.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.oauth2.main.sso.producers.SingleSignOnProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * This is useful when accessing an OAuth2 application directly from Front-Channel (NON-SOA)
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SingleSignOnEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SingleSignOnEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new SingleSignOnProducer( this );
    }
}