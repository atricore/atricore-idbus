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

public class IdentityProvider extends LocalProvider {

	private static final long serialVersionUID = 141137856095909986L;

    private boolean signAuthenticationAssertions;

    private boolean encryptAuthenticationAssertions;

    private Set<FederatedConnection> federatedConnections;

    // RFU
    private AttributeProfile attributeProfile;

    // RFU
    private AuthenticationMechanism authenticationMechanism;

    // RFU
    private AuthenticationContract authenticationContract;

    // RFU
    private AuthenticationAssertionEmissionPolicy emissionPolicy;

    private IdentityLookup identityLookup;

    private Set<Binding> activeBindings;

    private Set<Profile> activeProfiles;

    @Override
    public ProviderRole getRole() {
        return ProviderRole.SSOIdentityProvider;
    }

    @Override
    public void setRole(ProviderRole role) {
        throw new UnsupportedOperationException("Cannot change provider role");
    }

    public boolean isSignAuthenticationAssertions() {
        return signAuthenticationAssertions;
    }

    public void setSignAuthenticationAssertions(boolean signAuthenticationAssertions) {
        this.signAuthenticationAssertions = signAuthenticationAssertions;
    }

    public boolean isEncryptAuthenticationAssertions() {
        return encryptAuthenticationAssertions;
    }

    public void setEncryptAuthenticationAssertions(boolean encryptAuthenticationAssertions) {
        this.encryptAuthenticationAssertions = encryptAuthenticationAssertions;
    }

    public Set<FederatedConnection> getFederatedConnections() {
        return federatedConnections;
    }

    public void setFederatedConnections(Set<FederatedConnection> federatedConnections) {
        this.federatedConnections = federatedConnections;
    }

    public AttributeProfile getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfile attributeProfile) {
        this.attributeProfile = attributeProfile;
    }

    public AuthenticationMechanism getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanism authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
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

    public IdentityLookup getIdentityLookup() {
        return identityLookup;
    }

    public void setIdentityLookup(IdentityLookup identityLookup) {
        this.identityLookup = identityLookup;
    }

}
