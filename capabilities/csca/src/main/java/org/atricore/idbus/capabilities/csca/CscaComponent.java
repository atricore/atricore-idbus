package org.atricore.idbus.capabilities.csca;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.csca.endpoints.CscaNegotiationEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

public class CscaComponent  extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( CscaComponent.class );

    public CscaComponent() {
        super();
    }

    public CscaComponent(CamelContext camelContext) {
        super(camelContext);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        logger.debug("Creating CSCA Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;
        endpoint = new CscaNegotiationEndpoint(uri, this, parameters);
        return endpoint;
    }

}

