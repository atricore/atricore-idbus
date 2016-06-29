package org.atricore.idbus.capabilities.oauth2.main;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Service;
import org.atricore.idbus.capabilities.oauth2.main.authorization.endpoints.AuthorizationEndpoint;
import org.atricore.idbus.capabilities.oauth2.main.sso.endpoints.AssertionConsumerEndpoint;
import org.atricore.idbus.capabilities.oauth2.main.sso.endpoints.SingleLogoutEndpoint;
import org.atricore.idbus.capabilities.oauth2.main.sso.endpoints.SingleSignOnEndpoint;
import org.atricore.idbus.capabilities.oauth2.main.token.endpoints.TokenEndpoint;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Component extends DefaultComponent {

    private static final Log logger = LogFactory.getLog(OAuth2Component.class);

    public OAuth2Component() {
    }

    public OAuth2Component( CamelContext context ) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint = null;
        OAuth2Service e = getOAuth2Service(remaining);

        switch ( e ) {
            case SSOAssertionConsumerService:
                endpoint = new AssertionConsumerEndpoint(uri, this, parameters);
                break;
            case SSOSingleSignOnService:
                endpoint = new SingleSignOnEndpoint(uri, this, parameters);
                break;
            case SSOSingleLogoutService:
                endpoint = new SingleLogoutEndpoint(uri, this, parameters);
                break;
            case AuthorizationService:
                endpoint = new AuthorizationEndpoint(uri, this , parameters);
                break;
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

    protected OAuth2Service getOAuth2Service(String remaining) {

        // TODO !
        for (OAuth2Service et : OAuth2Service.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid OAUTH 2 service type " + remaining );
    }
}

