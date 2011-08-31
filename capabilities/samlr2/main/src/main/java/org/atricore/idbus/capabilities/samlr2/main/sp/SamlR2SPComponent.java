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

package org.atricore.idbus.capabilities.samlr2.main.sp;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.samlr2.main.binding.endpoints.ArtifactResolutionEndpoint;
import org.atricore.idbus.capabilities.samlr2.main.sp.endpoints.*;
import org.atricore.idbus.capabilities.samlr2.support.metadata.SamlR2Service;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * SAMLR2 Camel component.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Component.java 1246 2009-06-05 20:30:58Z sgonzalez $
 */
public class SamlR2SPComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( SamlR2SPComponent.class );

    public SamlR2SPComponent() {
    }

    public SamlR2SPComponent( CamelContext context ) {
        super( context );
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters)
            throws Exception {

        logger.debug("Creating Camel Endpoint for [" + uri + "] [" + remaining + "]");
        
        AbstractCamelEndpoint endpoint;
        SamlR2Service e = getSamlR2Service( remaining );

        switch ( e ) {
            case SingleLogoutService:
                endpoint = new SingleLogoutEndpoint( uri, this, parameters );
                break;
            case AssertionConsumerService:
                endpoint = new AssertionConsumerEndpoint( uri, this, parameters );
                break;
            case ArtifactResolutionService:
                endpoint = new ArtifactResolutionEndpoint( uri, this, parameters);
                break;
            case SPInitiatedSingleSignOnService:
                endpoint = new SPInitiatedSingleSignOnEndpoint(uri, this, parameters);
                break;
            case SPInitiatedSingleLogoutService:
                endpoint = new SPInitiatedSingleLogoutEndpoint(uri, this, parameters);
                break;
            case ManageNameIDService:
            	endpoint = new SPNameIDManagementEndpoint(uri, this, parameters);
            	break;
            case SPInitiatedManageNameIDService:
            	endpoint = new SPInitiatedNameIDManagementEndpoint(uri, this, parameters);
            	break;
            case AssertIdentityWithSimpleAuthenticationService:
                endpoint = new AssertIdentityWithSimpleAuthenticationEndpoint(uri, this, parameters);
                break;
            case SPSessionHeartBeatService:
                endpoint = new SessionHeartBeatEndpoint(uri, this, parameters);
                break;
            case SPCredentialsCallbackService:
                endpoint = new SPCredentialsCallbackEndpoint(uri, this, parameters);
                break;
            default:
                throw new IllegalArgumentException( "Unsupported SAMLR 2.0 endpoint " + remaining );
        }

        endpoint.setAction( remaining );
        setProperties( endpoint, parameters );

        return endpoint;
    }

    protected SamlR2Service getSamlR2Service(String remaining) {

        // TODO !
        for (SamlR2Service et : SamlR2Service.values()) {
            if (et.getQname().getLocalPart().equals(remaining))
                return et;
        }
        throw new IllegalArgumentException( "Invalid SAMLR2 endpoint specified for endpoint " + remaining );
    }
}
