package org.atricore.idbus.capabilities.openid.main.claims;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.claims.endpoints.OpenIDClaimsEndpoint;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.claims.SSOClaimsComponent;
import org.atricore.idbus.capabilities.sso.main.claims.endpoints.PreAuthenticationSecurityTokenClaimsEndpoint;
import org.atricore.idbus.capabilities.sso.main.claims.endpoints.SpUsernamePasswordClaimsEndpoint;
import org.atricore.idbus.capabilities.sso.main.claims.endpoints.UsernamePasscodeClaimsEndpoint;
import org.atricore.idbus.capabilities.sso.main.claims.endpoints.UsernamePasswordClaimsEndpoint;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * Created by sgonzalez on 2/24/14.
 */
public class OpenIDClaimsComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog(SSOClaimsComponent.class);

    public OpenIDClaimsComponent() {
        super();
    }

    public OpenIDClaimsComponent(CamelContext camelContext) {
        super(camelContext);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;

        AuthnCtxClass authnCtx = resolveAuthnCtx(uri, remaining);
        switch(authnCtx) {
            case OPENID_AUTHN_CTX:
                endpoint = new OpenIDClaimsEndpoint(uri, this, parameters);
                break;

            default:
                throw new SSOException("Unsupported endpoint type " + remaining);

        }

        return endpoint;

    }


    protected AuthnCtxClass resolveAuthnCtx(String uri, String remaining) {

        String ending = ":" + remaining;
        for (AuthnCtxClass authnCtx : AuthnCtxClass.values()) {
            if (authnCtx.getValue().endsWith(ending))
                return authnCtx;
        }
        throw new IllegalArgumentException("AuthnContextClass not supported " + remaining);
    }
}
