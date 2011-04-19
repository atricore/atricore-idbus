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

package org.atricore.idbus.capabilities.samlr2.main.claims;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.SamlR2Exception;
import org.atricore.idbus.capabilities.samlr2.main.claims.endpoints.UsernamePasscodeClaimsEndpoint;
import org.atricore.idbus.capabilities.samlr2.main.claims.endpoints.UsernamePasswordClaimsEndpoint;
import org.atricore.idbus.capabilities.samlr2.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SamlR2ClaimsComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( SamlR2ClaimsComponent.class );

    public SamlR2ClaimsComponent() {
        super();
    }

    public SamlR2ClaimsComponent(CamelContext camelContext) {
        super(camelContext);
    }                                

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;

        AuthnCtxClass authnCtx = resolveAuthnCtx(uri, remaining);
        switch(authnCtx) {
            case PASSWORD_AUTHN_CTX:
                endpoint = new UsernamePasswordClaimsEndpoint(uri, this, parameters);
                break;

            case TIME_SYNC_TOKEN_AUTHN_CTX:
                endpoint = new UsernamePasscodeClaimsEndpoint(uri, this, parameters);
                break;
            default:
                throw new SamlR2Exception("Unsupported endpoint type " + remaining);
                
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
