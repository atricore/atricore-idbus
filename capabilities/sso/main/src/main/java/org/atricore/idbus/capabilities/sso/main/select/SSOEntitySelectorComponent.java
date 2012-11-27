package org.atricore.idbus.capabilities.sso.main.select;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.select.endpoints.IdPSelectorEndpoint;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 */
public class SSOEntitySelectorComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog(SSOEntitySelectorComponent.class);

    public SSOEntitySelectorComponent() {
    }

    public SSOEntitySelectorComponent(CamelContext context) {
        super( context );
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;
        SSOService e = getSamlR2Service( remaining );

        switch ( e ) {
            case IdPSelectorService:
                endpoint = new IdPSelectorEndpoint(uri, this, parameters);
                break;
            default:
                throw new IllegalArgumentException( "Invalid SSO Entity Selector endpoint specified for endpoint " + remaining );
        }

        endpoint.setAction( remaining );
        setProperties( endpoint, parameters );

        return endpoint;

    }

    protected SSOService getSamlR2Service(String remaining) {

        for (SSOService et : SSOService.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid SAMLR2 endpoint specified for endpoint " + remaining );
    }
}
