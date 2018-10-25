package org.atricore.idbus.capabilities.sso.main.claims.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.claims.producers.SpUsernamePasswordClaimsProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpUsernamePasswordClaimsEndpoint  extends AbstractCamelEndpoint {

    public SpUsernamePasswordClaimsEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new SpUsernamePasswordClaimsProducer( this );
    }
}