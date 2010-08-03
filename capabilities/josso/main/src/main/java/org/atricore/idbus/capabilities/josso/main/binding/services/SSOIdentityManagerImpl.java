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

import org.josso.gateway.ws._1_2.wsdl.InvalidSessionErrorMessage;
import org.josso.gateway.ws._1_2.wsdl.NoSuchUserErrorMessage;
import org.josso.gateway.ws._1_2.wsdl.SSOIdentityManager;
import org.josso.gateway.ws._1_2.wsdl.SSOIdentityManagerErrorMessage;

@javax.jws.WebService(
                      serviceName = "SSOIdentityManagerWS",
                      portName = "SSOIdentityManagerSoap",
                      targetNamespace = "urn:org:josso:gateway:ws:1.2:wsdl",
                      endpointInterface = "org.josso.gateway.ws._1_2.wsdl.SSOIdentityManager")

public class SSOIdentityManagerImpl implements SSOIdentityManager {

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityManager#findUserInSecurityDomain(org.josso.gateway.ws._1_2.protocol.FindUserInSecurityDomainRequestType  findUserInSecurityDomainRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.FindUserInSecurityDomainResponseType findUserInSecurityDomain(org.josso.gateway.ws._1_2.protocol.FindUserInSecurityDomainRequestType findUserInSecurityDomainRequest) throws NoSuchUserErrorMessage , SSOIdentityManagerErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityManager#userExists(org.josso.gateway.ws._1_2.protocol.UserExistsRequestType  userExistsRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.UserExistsResponseType userExists(org.josso.gateway.ws._1_2.protocol.UserExistsRequestType userExistsRequest) throws SSOIdentityManagerErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityManager#findUserInSession(org.josso.gateway.ws._1_2.protocol.FindUserInSessionRequestType  findUserInSessionRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.FindUserInSessionResponseType findUserInSession(org.josso.gateway.ws._1_2.protocol.FindUserInSessionRequestType findUserInSessionRequest) throws NoSuchUserErrorMessage , InvalidSessionErrorMessage , SSOIdentityManagerErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityManager#findRolesBySSOSessionId(org.josso.gateway.ws._1_2.protocol.FindRolesBySSOSessionIdRequestType  findRolesBySSOSessionIdRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.FindRolesBySSOSessionIdResponseType findRolesBySSOSessionId(org.josso.gateway.ws._1_2.protocol.FindRolesBySSOSessionIdRequestType findRolesBySSOSessionIdRequest) throws InvalidSessionErrorMessage , SSOIdentityManagerErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

}
