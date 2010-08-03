package org.atricore.idbus.capabilities.samlr2.main.sp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.samlr2.main.sp.producers.AssertIdentityWithSimpleAuthenticationProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AssertIdentityWithSimpleAuthenticationEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public AssertIdentityWithSimpleAuthenticationEndpoint(String endpointURI, Component component, Map parameters) throws Exception {
        super(endpointURI, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer() throws Exception {
        return new AssertIdentityWithSimpleAuthenticationProducer(this);
    }
}
