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

package org.atricore.idbus.capabilities.openid.main.sp;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.sp.endpoints.*;
import org.atricore.idbus.capabilities.openid.main.support.OpenIDService;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * OpenID Camel component.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id: OpenIDComponent.java 1246 2009-06-05 20:30:58Z gbrigand $
 */
public class OpenIDSPComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( OpenIDSPComponent.class );

    public OpenIDSPComponent() {
    }

    public OpenIDSPComponent( CamelContext context ) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");
        
        AbstractCamelEndpoint endpoint;
        OpenIDService e = getOpenIDService( remaining );

        switch ( e ) {
            case SPInitiatedSingleSignOnService:
                endpoint = new SPInitiatedSingleSignOnEndpoint(uri, this, parameters);
                break;
            case RelyingPartyService:
                endpoint = new RelyingPartyEndpoint(uri, this, parameters);
                break;
            default:
                throw new IllegalArgumentException( "Unsupported OpenID endpoint " + remaining );
        }

        endpoint.setAction( remaining );
        setProperties( endpoint, parameters );

        return endpoint;
    }

    protected OpenIDService getOpenIDService(String remaining) {

        for (OpenIDService et : OpenIDService.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid OpenID endpoint specified for endpoint " + remaining );
    }
}
