package org.atricore.idbus.capabilities.sso.main.claims.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.claims.producers.PreAuthenticationClaimsProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class PreAuthenticationSecurityTokenClaimsEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public PreAuthenticationSecurityTokenClaimsEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new PreAuthenticationClaimsProducer( this );
    }
}