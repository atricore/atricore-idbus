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

public class ServiceProvider extends LocalProvider {

	private static final long serialVersionUID = 1096573594152761313L;

    private Set<FederatedConnection> federatedConnections;

    private Activation activation;

    private IdentityLookup identityLookup;
    
    private IdentitySource identitySource;

    private AccountLinkagePolicy accountLinkagePolicy;

    // RFU
    private AuthenticationContract authenticationContract;

    // RFU
    private AuthenticationMechanism authenticationMechanism;

    
    @Override
    public ProviderRole getRole() {
        return ProviderRole.SSOServiceProvider;
    }

    @Override
    public void setRole(ProviderRole role) {
        throw new UnsupportedOperationException("Cannot change provider role");
    }

    public Set<FederatedConnection> getFederatedConnections() {
        return federatedConnections;
    }

    public void setFederatedConnections(Set<FederatedConnection> federatedConnections) {
        this.federatedConnections = federatedConnections;
    }

    public Activation getActivation() {
        return activation;
    }

    public void setActivation(Activation activation) {
        this.activation = activation;
    }

    public IdentityLookup getIdentityLookup() {
        return identityLookup;
    }

    public void setIdentityLookup(IdentityLookup identityLookup) {
        this.identityLookup = identityLookup;
    }
}
