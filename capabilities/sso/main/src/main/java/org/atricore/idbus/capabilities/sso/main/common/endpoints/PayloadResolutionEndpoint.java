package org.atricore.idbus.capabilities.sso.main.common.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.common.producers.MetadataProducer;
import org.atricore.idbus.capabilities.sso.main.common.producers.PayloadResolutionProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

public class PayloadResolutionEndpoint<E> extends AbstractCamelEndpoint<CamelMediationExchange> {

    public PayloadResolutionEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new PayloadResolutionProducer( this );
    }
}
