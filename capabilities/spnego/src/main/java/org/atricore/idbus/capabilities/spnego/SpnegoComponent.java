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

package org.atricore.idbus.capabilities.spnego;

import org.atricore.idbus.capabilities.spnego.endpoints.SpnegoNegotiationEndpoint;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

import java.util.Map;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SpnegoComponent extends DefaultComponent {

    private static final Log logger = LogFactory.getLog( SpnegoComponent.class );

    public SpnegoComponent() {
        super();
    }

    public SpnegoComponent(CamelContext camelContext) {
        super(camelContext);
    }                                

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        logger.debug("Creating SPNEGO Endpoint for [" + uri + "] [" + remaining + "]");

        AbstractCamelEndpoint endpoint;
        endpoint = new SpnegoNegotiationEndpoint(uri, this, parameters);
        return endpoint;
    }

}
