/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.capabilities.sso.main.claims;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.SSOException;
import org.atricore.idbus.capabilities.sso.main.claims.endpoints.*;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SSOClaimsComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( SSOClaimsComponent.class );

    public SSOClaimsComponent() {
        super();
    }

    public SSOClaimsComponent(CamelContext camelContext) {
        super(camelContext);
    }                                

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;

        AuthnCtxClass authnCtx = resolveAuthnCtx(uri, remaining);
        switch(authnCtx) {
            case PASSWORD_AUTHN_CTX:
            case PPT_AUTHN_CTX:
                endpoint = new UsernamePasswordClaimsEndpoint(uri, this, parameters);
                break;

            case TIME_SYNC_TOKEN_AUTHN_CTX:
            case TELEPHONY_AUTHN_CTX:
            case PERSONAL_TELEPHONY_AUTHN_CTX:
            case HOTP_CTX:
            case MTFU_AUTHN_CTX:
            case MTFC_AUTHN_CTX:
                endpoint = new UsernamePasscodeClaimsEndpoint(uri, this, parameters);
                break;

            case ATC_SP_PASSWORD_AUTHN_CTX:
                endpoint = new SpUsernamePasswordClaimsEndpoint(uri, this, parameters);
                break;

            case OAUTH2_PREAUTHN_CTX:
                endpoint = new PreAuthenticationSecurityTokenClaimsEndpoint(uri, this, parameters);
                break;

            case OAUTH2_PREAUTHN_PASSIVE_CTX:
                endpoint = new PreAuthenticationSecurityTokenClaimsEndpoint(uri, this, parameters);
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
