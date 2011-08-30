package org.atricore.idbus.capabilities.samlr2.main.claims.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.samlr2.main.claims.producers.SpUsernamePasswordClaimsProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpUsernamePasswordClaimsEndpoint  extends AbstractCamelEndpoint<CamelMediationExchange> {

    public SpUsernamePasswordClaimsEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new SpUsernamePasswordClaimsProducer( this );
    }
}