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

package org.atricore.idbus.kernel.main.mediation.camel.component.binding;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultComponent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MediationBindingComponent extends DefaultComponent {

    private static Log logger = LogFactory.getLog(MediationBindingComponent.class);


    public MediationBindingComponent() {
    }

    public MediationBindingComponent(CamelContext camelContext) {
        super(camelContext);
    }

    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        String binding = (String) parameters.get("binding");
        logger.debug("Creating endpoint for Binding " + binding);
        return createEndpoint(binding, remaining, uri);
    }

    protected Endpoint createEndpoint(String binding,
                                                      String directEndpointUri,
                                                      String uri) throws Exception {

        logger.debug("Creating binding endpoint for " + binding);
        return new CamelMediationEndpoint(uri, directEndpointUri, this);
    }


}
