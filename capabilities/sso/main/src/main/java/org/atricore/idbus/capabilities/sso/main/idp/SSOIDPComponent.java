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

package org.atricore.idbus.capabilities.sso.main.idp;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sso.main.binding.endpoints.ArtifactResolutionEndpoint;
import org.atricore.idbus.capabilities.sso.main.idp.endpoints.*;
import org.atricore.idbus.capabilities.sso.support.metadata.SSOService;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * SAMLR2 Camel component.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SSOIDPComponent.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public class SSOIDPComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( SSOIDPComponent.class );

    public SSOIDPComponent() {
    }

    public SSOIDPComponent(CamelContext context) {
        super( context );
    }


    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");
        
        AbstractCamelEndpoint endpoint;
        SSOService e = getSamlR2Service( remaining );

        switch ( e ) {
            case SingleSignOnService:
                endpoint = new SingleSignOnEndpoint( uri, this, parameters );
                break;
            case SingleLogoutService:
                endpoint = new SingleLogoutEndpoint( uri, this, parameters );
                break;
            case ArtifactResolutionService:
                endpoint = new ArtifactResolutionEndpoint( uri, this, parameters);
                break;
            case IDPInitiatedSingleLogoutService:
                endpoint = new IdPInitiatedSingleLogoutEndpoint( uri, this, parameters );
                break;
            case IDPSessionHeartBeatService:
                endpoint = new SessionHeartBeatEndpoint(uri, this, parameters);
                break;
            case ProxyAssertionConsumerService:
                endpoint = new SingleSignOnEndpoint(uri, this, parameters);
                break;
            case ProxySingleLogoutService:
                endpoint = new SingleLogoutEndpoint(uri, this, parameters);
                break;
            case IdPSelectorCallbackService:
                endpoint = new IdPSelectorCallbackEndpoint(uri, this, parameters);
                break;

            default:
                throw new IllegalArgumentException( "Invalid SAMLR2 endpoint specified for endpoint " + remaining );
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
