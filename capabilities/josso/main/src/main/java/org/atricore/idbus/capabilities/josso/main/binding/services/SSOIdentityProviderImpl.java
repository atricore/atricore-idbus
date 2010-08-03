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

import org.josso.gateway.ws._1_2.wsdl.AssertionNotValidErrorMessage;
import org.josso.gateway.ws._1_2.wsdl.SSOIdentityProvider;
import org.josso.gateway.ws._1_2.wsdl.SSOIdentityProviderErrorMessage;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
@javax.jws.WebService(
                      serviceName = "SSOIdentityProviderWS",
                      portName = "SSOIdentityProviderSoap",
                      targetNamespace = "urn:org:josso:gateway:ws:1.2:wsdl",
                      endpointInterface = "org.josso.gateway.ws._1_2.wsdl.SSOIdentityProvider")
public class SSOIdentityProviderImpl implements SSOIdentityProvider {

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityProvider#globalSignoff(org.josso.gateway.ws._1_2.protocol.GlobalSignoffRequestType  globalSignoffRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.GlobalSignoffResponseType globalSignoff(org.josso.gateway.ws._1_2.protocol.GlobalSignoffRequestType globalSignoffRequest) throws SSOIdentityProviderErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityProvider#resolveAuthenticationAssertion(org.josso.gateway.ws._1_2.protocol.ResolveAuthenticationAssertionRequestType  resolveAuthenticationAssertionRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.ResolveAuthenticationAssertionResponseType resolveAuthenticationAssertion(org.josso.gateway.ws._1_2.protocol.ResolveAuthenticationAssertionRequestType resolveAuthenticationAssertionRequest) throws SSOIdentityProviderErrorMessage , AssertionNotValidErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

    /* (non-Javadoc)
     * @see org.josso.gateway.ws._1_2.wsdl.SSOIdentityProvider#assertIdentityWithSimpleAuthentication(org.josso.gateway.ws._1_2.protocol.AssertIdentityWithSimpleAuthenticationRequestType  assertIdentityWithSimpleAuthenticationRequest )*
     */
    public org.josso.gateway.ws._1_2.protocol.AssertIdentityWithSimpleAuthenticationResponseType assertIdentityWithSimpleAuthentication(org.josso.gateway.ws._1_2.protocol.AssertIdentityWithSimpleAuthenticationRequestType assertIdentityWithSimpleAuthenticationRequest) throws SSOIdentityProviderErrorMessage    {
        throw new UnsupportedOperationException("Not intended to be executed!");
    }

}
