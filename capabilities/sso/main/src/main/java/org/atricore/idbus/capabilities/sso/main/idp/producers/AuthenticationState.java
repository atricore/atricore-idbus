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

package org.atricore.idbus.capabilities.sso.main.idp.producers;

import oasis.names.tc.saml._2_0.protocol.AuthnRequestType;
import org.atricore.idbus.capabilities.sso.support.auth.AuthnCtxClass;
import org.atricore.idbus.kernel.main.mediation.endpoint.IdentityMediationEndpoint;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AuthenticationState implements java.io.Serializable {

    public AuthenticationState() {
    }

    /**
     * SAML received relay state, can be null
     */
    private String receivedRelayState;

    private IdentityMediationEndpoint currentClaimsEndpoint;

    private int currentClaimsEndpointTryCount;

    private Set<String> usedClaimsEndpoints = new HashSet<String>();

    private IdentityMediationEndpoint currentIdConfirmationEndpoint;

    private int currentIdConfirmationEndpointTryCount;

    private Set<String> usedIdConfirmationEndpoints = new HashSet<String>();

    private String responseMode;

    private String responseFormat;

    // The last authn request
    private AuthnRequestType authnRequest;

    public AuthnRequestType getAuthnRequest() {
        return authnRequest;
    }

    public void setAuthnRequest(AuthnRequestType authnRequest) {
        this.authnRequest = authnRequest;
    }

    public String getReceivedRelayState() {
        return receivedRelayState;
    }

    public void setReceivedRelayState(String receivedRelayState) {
        this.receivedRelayState = receivedRelayState;
    }

    public IdentityMediationEndpoint getCurrentClaimsEndpoint() {
        return currentClaimsEndpoint;
    }

    public void setCurrentClaimsEndpoint(IdentityMediationEndpoint currentClaimsEndpoint) {
        this.currentClaimsEndpoint = currentClaimsEndpoint;
    }

    public int getCurrentClaimsEndpointTryCount() {
        return currentClaimsEndpointTryCount;
    }

    public void setCurrentClaimsEndpointTryCount(int currentClaimsEndpointTryCount) {
        this.currentClaimsEndpointTryCount = currentClaimsEndpointTryCount;
    }

    public Set<String> getUsedClaimsEndpoints() {
        return usedClaimsEndpoints;
    }

    public AuthnCtxClass getCurrentAuthnCtxClass() {
        if (currentClaimsEndpoint != null)
            return AuthnCtxClass.asEnum(currentClaimsEndpoint.getType());

        return null;
    }

    public IdentityMediationEndpoint getCurrentIdConfirmationEndpoint() {
        return currentIdConfirmationEndpoint;
    }

    public void setCurrentIdConfirmationEndpoint(IdentityMediationEndpoint currentIdConfirmationEndpoint) {
        this.currentIdConfirmationEndpoint = currentIdConfirmationEndpoint;
    }

    public int getCurrentIdConfirmationEndpointTryCount() {
        return currentIdConfirmationEndpointTryCount;
    }

    public void setCurrentIdConfirmationEndpointTryCount(int currentIdConfirmationEndpointTryCount) {
        this.currentIdConfirmationEndpointTryCount = currentIdConfirmationEndpointTryCount;
    }

    public Set<String> getUsedIdConfirmationEndpoints() {
        return usedIdConfirmationEndpoints;
    }

    public void setUsedIdConfirmationEndpoints(Set<String> usedIdConfirmationEndpoints) {
        this.usedIdConfirmationEndpoints = usedIdConfirmationEndpoints;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    public String getResponseFormat() {
        return responseFormat;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }
}
