package org.atricore.idbus.capabilities.samlr2.main.binding.endpoints;

import org.apache.camel.Component;
import org.apache.camel.Producer;
import org.atricore.idbus.capabilities.samlr2.main.binding.producers.ArtifactResolutionProducer;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.component.binding.CamelMediationExchange;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ArtifactResolutionEndpoint extends AbstractCamelEndpoint<CamelMediationExchange> {

    public ArtifactResolutionEndpoint(String uri, Component component, Map parameters ) throws Exception {
        super(uri, component, parameters);
    }

    public Producer<CamelMediationExchange> createProducer () throws Exception {
        return new ArtifactResolutionProducer( this );
    }
}
