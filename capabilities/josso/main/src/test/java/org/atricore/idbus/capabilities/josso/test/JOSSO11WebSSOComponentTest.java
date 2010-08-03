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

package org.atricore.idbus.capabilities.josso.test;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Endpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.camel.AbstractCamelEndpoint;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: JOSSO11WebSSOComponentTest.java 1182 2009-05-05 20:28:51Z ajadzinsky $
 */
public class JOSSO11WebSSOComponentTest extends ContextTestSupport {
    private static Log log = LogFactory.getLog(JOSSO11WebSSORouteTest.class);


    public void testJOSSO11WebSSOEndpointsAreConfiguredProperly() throws Exception {
        AbstractCamelEndpoint endpoint = resolveMandatoryEndpoint("josso-binding:JOSSO11AuthnRequestToSAMLR2?channelRef=ABC");
        assertEquals("getChannelRef", "ABC", endpoint.getChannelRef());
    }

    @Override
    protected AbstractCamelEndpoint resolveMandatoryEndpoint(String uri) {
        Endpoint endpoint = super.resolveMandatoryEndpoint(uri);
        return assertIsInstanceOf( AbstractCamelEndpoint.class, endpoint);
    }


}
