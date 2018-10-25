package org.atricore.idbus.capabilities.sso.main.claims.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.claims.producers.UsernamePasscodeClaimsProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */

public class UsernamePasscodeClaimsEndpoint extends AbstractCamelEndpoint {
    public UsernamePasscodeClaimsEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new UsernamePasscodeClaimsProducer( this );
    }
}