package org.atricore.idbus.capabilities.samlr2.main.sp.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.samlr2.main.sp.producers.SPCredentialsCallbackProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SPCredentialsCallbackEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

	public SPCredentialsCallbackEndpoint(String endpointURI, Component component, Map parameters) throws Exception {
		super(endpointURI, component, parameters);
	}

	public Producer createProducer() throws Exception {
		return new SPCredentialsCallbackProducer( this );
	}

}
