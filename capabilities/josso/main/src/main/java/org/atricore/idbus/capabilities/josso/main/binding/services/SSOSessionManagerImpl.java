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

package org.atricore.idbus.capabilities.josso.main.binding.services;

import org.josso.gateway.ws._1_2.wsdl.NoSuchSessionErrorMessage;
import org.josso.gateway.ws._1_2.wsdl.SSOSessionErrorMessage;
import org.josso.gateway.ws._1_2.wsdl.SSOSessionManager;

@javax.jws.WebService(
                      serviceName = "SSOSessionManagerWS",
                      portName = "SSOSessionManagerSoap",
                      targetNamespace = "urn:org:josso:gateway:ws:1.2:wsdl",
                      endpointInterface = "org.josso.gateway.ws._1_2.wsdl.SSOSessionManager")

public class SSOSessionManagerImpl implements SSOSessionManager {

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOSessionManager#getSession(org.josso.gateway.ws._1_2.protocol.SessionRequestType  sessionRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.SessionResponseType getSession(org.josso.gateway.ws._1_2.protocol.SessionRequestType sessionRequest) throws NoSuchSessionErrorMessage , SSOSessionErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOSessionManager#accessSession(org.josso.gateway.ws._1_2.protocol.AccessSessionRequestType  accessSessionRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.AccessSessionResponseType accessSession(org.josso.gateway.ws._1_2.protocol.AccessSessionRequestType accessSessionRequest) throws NoSuchSessionErrorMessage , SSOSessionErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

}
