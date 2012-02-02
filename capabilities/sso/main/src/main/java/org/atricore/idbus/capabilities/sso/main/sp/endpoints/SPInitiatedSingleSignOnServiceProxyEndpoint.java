package org.atricore.idbus.capabilities.sso.main.sp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.sp.producers.SPInitiatedSingleSignOnProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SPInitiatedSingleSignOnServiceProxyEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SPInitiatedSingleSignOnServiceProxyEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    // Just re-use the SP Initiated SSO Producer
    public Producer createProducer () throws Exception {
        return new SPInitiatedSingleSignOnProducer( this );
    }
}
