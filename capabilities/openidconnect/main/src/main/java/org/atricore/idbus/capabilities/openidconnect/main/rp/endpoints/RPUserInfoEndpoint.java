package org.atricore.idbus.capabilities.openidconnect.main.rp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.openidconnect.main.rp.producer.UserInfoProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

public class RPUserInfoEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public RPUserInfoEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new UserInfoProducer( this );
    }
}
