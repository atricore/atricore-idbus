/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.util.Set;

public class IdentityProvider extends FederatedProvider {

	private static final long serialVersionUID = 141137856095909986L;

    private boolean wantAuthnRequestsSigned;

    private boolean signRequests;

    private boolean wantSignedRequests;
    
    // RFU
    private AttributeProfile attributeProfile;

    private Set<AuthenticationMechanism> authenticationMechanisms;

    // RFU
    private AuthenticationContract authenticationContract;

    // RFU
    private AuthenticationAssertionEmissionPolicy emissionPolicy;

    // RFU
    //TODO check whether LocalProvider will have bindings or IdentityProvider
//    private Set<Binding> activeBindings;

    // RFU
    //TODO check whether LocalProvider will have profiles or IdentityProvider    
//    private Set<Profile> activeProfiles;

    private DelegatedAuthentication delegatedAuthentication;
    
    @Override
    public ProviderRole getRole() {
        return ProviderRole.SSOIdentityProvider;
    }

    @Override
    public void setRole(ProviderRole role) {
        throw new UnsupportedOperationException("Cannot change provider role");
    }

    public boolean isWantAuthnRequestsSigned() {
        return wantAuthnRequestsSigned;
    }

    public void setWantAuthnRequestsSigned(boolean wantAuthnRequestsSigned) {
        this.wantAuthnRequestsSigned = wantAuthnRequestsSigned;
    }

    public boolean isSignRequests() {
        return signRequests;
    }

    public void setSignRequests(boolean signRequests) {
        this.signRequests = signRequests;
    }

    public boolean isWantSignedRequests() {
        return wantSignedRequests;
    }

    public void setWantSignedRequests(boolean wantSignedRequests) {
        this.wantSignedRequests = wantSignedRequests;
    }

    public AttributeProfile getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfile attributeProfile) {
        this.attributeProfile = attributeProfile;
    }


    public Set<AuthenticationMechanism> getAuthenticationMechanisms() {
        return authenticationMechanisms;
    }

    public void setAuthenticationMechanisms(Set<AuthenticationMechanism> authenticationMechanisms) {
        this.authenticationMechanisms = authenticationMechanisms;
    }

    public AuthenticationContract getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContract authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public AuthenticationAssertionEmissionPolicy getEmissionPolicy() {
        return emissionPolicy;
    }

    public void setEmissionPolicy(AuthenticationAssertionEmissionPolicy emissionPolicy) {
        this.emissionPolicy = emissionPolicy;
    }

    public DelegatedAuthentication getDelegatedAuthentication() {
        return delegatedAuthentication;
    }

    public void setDelegatedAuthentication(DelegatedAuthentication delegatedAuthentication) {
        this.delegatedAuthentication = delegatedAuthentication;
    }
}
