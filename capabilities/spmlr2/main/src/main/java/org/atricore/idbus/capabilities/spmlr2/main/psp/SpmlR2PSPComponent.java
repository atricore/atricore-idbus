package org.atricore.idbus.capabilities.spmlr2.main.psp;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.spmlr2.main.SpmlR2Service;
import org.atricore.idbus.capabilities.spmlr2.main.psp.endpoints.PSPEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpmlR2PSPComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( SpmlR2PSPComponent.class );

    public SpmlR2PSPComponent() {
    }

    public SpmlR2PSPComponent( CamelContext context ) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");
        
        AbstractCamelEndpoint endpoint;
        SpmlR2Service e = getSpmlR2Service( remaining );

        // For now, we have only one service for the entire functionality ..
        switch ( e ) {
            case PSPService:
                endpoint = new PSPEndpoint( uri, this, parameters );
                break;
            default:
                throw new IllegalArgumentException( "Unsupported SPML 2.0 endpoint " + remaining );
        }

        endpoint.setAction( remaining );
        setProperties( endpoint, parameters );

        return endpoint;
    }

    protected SpmlR2Service getSpmlR2Service(String remaining) {

        // TODO !
        for (SpmlR2Service et : SpmlR2Service.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid SPMLR2 endpoint specified for endpoint " + remaining );
    }
}
