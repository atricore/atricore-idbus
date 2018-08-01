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

package org.atricore.idbus.kernel.main.mediation.camel;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: AbstractCamelEndpoint.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public abstract class AbstractCamelEndpoint extends DefaultEndpoint {
    private static final Log logger = LogFactory.getLog(AbstractCamelEndpoint.class);

    protected String endpointRef;
    protected String channelRef;
    protected String action;
    protected boolean isResponse;

    protected AbstractCamelEndpoint(String endpointURI, Component component, Map parameters) throws Exception {
        super(endpointURI, component);
        CamelContext ctx = component.getCamelContext();
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean isSingleton() {
        return false;
    }

    public String getChannelRef() {
        return channelRef;
    }

    public void setChannelRef(String channelRef) {
        this.channelRef = channelRef;
    }

    public String getEndpointRef() {
        return endpointRef;
    }

    public void setEndpointRef(String endpointRef) {
        this.endpointRef = endpointRef;
    }

    public boolean isResponse() {
        return isResponse;
    }

    public void setResponse(boolean response) {
        isResponse = response;
    }

    public String getAction() {
        if(action == null) {
            return this.getClass().getSimpleName().replace( "IdentityMediationEndpoint", "" );
        }
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}