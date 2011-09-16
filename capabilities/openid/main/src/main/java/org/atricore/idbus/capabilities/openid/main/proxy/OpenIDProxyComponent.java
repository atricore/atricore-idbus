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

package org.atricore.idbus.capabilities.openid.main.proxy;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openid.main.proxy.endpoints.*;
import org.atricore.idbus.capabilities.openid.main.support.OpenIDService;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * OpenID Service Provider Camel component.
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OpenIDProxyComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( OpenIDProxyComponent.class );

    public OpenIDProxyComponent() {
    }

    public OpenIDProxyComponent(CamelContext context) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");
        
        AbstractCamelEndpoint endpoint;
        OpenIDService e = getOpenIDService( remaining );

        switch ( e ) {
            case SPInitiatedSingleSignOnServiceProxy:
                endpoint = new OpenIDSingleSignOnProxyEndpoint(uri, this, parameters);
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
