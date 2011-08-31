package org.atricore.idbus.capabilities.sso.main.sp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.sso.main.sp.producers.ProxyAssertionConsumerProducer;
import org.atricore.idbus.capabilities.sso.main.sp.producers.SessionHeartBeatProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class ProxyAssertionConsumerEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public ProxyAssertionConsumerEndpoint(String uri, Component component, Map parameters) throws Exception {
        super(uri, component, parameters);
    }

    public Producer createProducer () throws Exception {
        return new ProxyAssertionConsumerProducer( this );
    }
}
