package org.atricore.idbus.capabilities.preauthn;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.preauthn.endpoints.PreAuthnEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

public class PreAuthnComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( PreAuthnComponent.class );

    public PreAuthnComponent() {
        super();
    }

    public PreAuthnComponent(CamelContext camelContext) {
        super(camelContext);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        logger.debug("Creating PRE-AUTHN Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;
        endpoint = new PreAuthnEndpoint(uri, this, parameters);
        return endpoint;
    }

}


