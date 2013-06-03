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

public class InternalSaml2ServiceProvider extends FederatedProvider {

	private static final long serialVersionUID = 1098843994152761313L;

    private ServiceConnection serviceConnection;

    private AccountLinkagePolicy accountLinkagePolicy;

    private IdentityMappingPolicy identityMappingPolicy;
    
    // RFU
    private AuthenticationContract authenticationContract;

    // RFU
    private Set<AuthenticationMechanism> authenticationMechanisms;

    private boolean signAuthenticationRequests;

    private boolean wantAssertionSigned;

    private boolean signRequests;

    private boolean wantSignedRequests;

    private boolean wantSLOResponseSigned;
    
    private int messageTtl;
    
    private int messageTtlTolerance;

    @Override
    public ProviderRole getRole() {
        return ProviderRole.SSOServiceProvider;
    }

    @Override
    public void setRole(ProviderRole role) {
        throw new UnsupportedOperationException("Cannot change provider role");
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    public AccountLinkagePolicy getAccountLinkagePolicy() {
        return accountLinkagePolicy;
    }

    public void setAccountLinkagePolicy(AccountLinkagePolicy accountLinkagePolicy) {
        this.accountLinkagePolicy = accountLinkagePolicy;
    }

    public IdentityMappingPolicy getIdentityMappingPolicy() {
        return identityMappingPolicy;
    }

    public void setIdentityMappingPolicy(IdentityMappingPolicy identityMappingPolicy) {
        this.identityMappingPolicy = identityMappingPolicy;
    }

    public AuthenticationContract getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContract authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public Set<AuthenticationMechanism> getAuthenticationMechanisms() {
        return authenticationMechanisms;
    }

    public void setAuthenticationMechanisms(Set<AuthenticationMechanism> authenticationMechanisms) {
        this.authenticationMechanisms = authenticationMechanisms;
    }

    public boolean isSignAuthenticationRequests() {
        return signAuthenticationRequests;
    }

    public void setSignAuthenticationRequests(boolean signAuthenticationRequests) {
        this.signAuthenticationRequests = signAuthenticationRequests;
    }

    public boolean isWantAssertionSigned() {
        return wantAssertionSigned;
    }

    public void setWantAssertionSigned(boolean wantAssertionSigned) {
        this.wantAssertionSigned = wantAssertionSigned;
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

    public boolean isWantSLOResponseSigned() {
        return wantSLOResponseSigned;
    }

    public void setWantSLOResponseSigned(boolean wantSLOResponseSigned) {
        this.wantSLOResponseSigned = wantSLOResponseSigned;
    }

    public int getMessageTtl() {
        return messageTtl;
    }

    public void setMessageTtl(int messageTtl) {
        this.messageTtl = messageTtl;
    }

    public int getMessageTtlTolerance() {
        return messageTtlTolerance;
    }

    public void setMessageTtlTolerance(int messageTtlTolerance) {
        this.messageTtlTolerance = messageTtlTolerance;
    }

}
