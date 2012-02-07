package org.atricore.idbus.capabilities.atricoreid.as.main;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.atricoreid.common.AtricoreIDService;
import org.atricore.idbus.capabilities.atricoreid.as.main.token.endpoints.TokenEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog(AtricoreIDComponent.class);

    public AtricoreIDComponent() {
    }

    public AtricoreIDComponent( CamelContext context ) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint = null;
        AtricoreIDService e = getAtricoreIDService(remaining);

        switch ( e ) {
            case TokenService:
                endpoint = new TokenEndpoint(uri, this, parameters);
                break;
            default:
                throw new IllegalArgumentException( "Unsupported OAUTH 2 service type " + remaining );
        }

        endpoint.setAction( remaining );
        setProperties( endpoint, parameters );

        return endpoint;
    }

    protected AtricoreIDService getAtricoreIDService(String remaining) {

        // TODO !
        for (AtricoreIDService et : AtricoreIDService.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid OAUTH 2 service type " + remaining );
    }
}

