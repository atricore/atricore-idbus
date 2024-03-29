package org.atricore.idbus.capabilities.openidconnect.main.proxy;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.common.OpenIDConnectService;
import org.atricore.idbus.capabilities.openidconnect.main.proxy.endpoints.*;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * Created by sgonzalez on 3/11/14.
 */
public class OpenIDConnectProxyComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog(OpenIDConnectProxyComponent.class);

    public OpenIDConnectProxyComponent() {
    }

    public OpenIDConnectProxyComponent(CamelContext context) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;
        OpenIDConnectService e = getOpenIDService( remaining );

        switch ( e ) {

            case SPInitiatedSingleSignOnServiceProxy:
                endpoint = new SingleSignOnProxyEndpoint(uri, this, parameters);
                break;

            case SPInitiatedAuhnServiceProxy:
                endpoint = new SPInitiatedEndpoint(uri, this, parameters);
                break;

            case ExtOpAuthzTokenConsumerServiceProxy:
            case AzureAuthzTokenConsumerServiceProxy:
                endpoint = new ProxyRPAuthzTokenConsumerEndpoint(uri, this, parameters);
                break;

            case GoogleAuthzTokenConsumerServiceProxy:
                endpoint = new GoogleAuthzTokenConsumerEndpoint(uri, this, parameters);
                break;

            case FacebookAuthzTokenConsumerServiceProxy:
                endpoint = new FacebookAuthzTokenConsumerEndpoint(uri, this, parameters);
                break;

            case TwitterAuthzTokenConsumerServiceProxy:
                endpoint = new TwitterAuthzTokenConsumerEndpoint(uri, this, parameters);
                break;

            case LinkedInAuthzTokenConsumerServiceProxy:
                endpoint = new LinkedInAuthzTokenConsumerEndpoint(uri, this, parameters);
                break;

            case WeChatAuthzTokenConsumerServiceProxy:
                endpoint = new WeChatAuthzTokenConsumerEndpoint(uri, this, parameters);
                break;


            default:
                throw new IllegalArgumentException( "Unsupported OpenID Connect endpoint " + remaining );
        }

        endpoint.setAction( remaining );
        setProperties( endpoint, parameters );

        return endpoint;
    }

    protected OpenIDConnectService getOpenIDService(String remaining) {

        for (OpenIDConnectService et : OpenIDConnectService.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid OpenID connect endpoint specified for endpoint " + remaining );
    }
}
