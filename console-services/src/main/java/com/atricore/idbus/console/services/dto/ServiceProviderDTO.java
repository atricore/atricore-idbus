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

package com.atricore.idbus.console.services.dto;


public class ServiceProviderDTO extends FederatedProviderDTO {

	private static final long serialVersionUID = 1096573594152761313L;

    private ActivationDTO activation;

    private ServiceConnectionDTO serviceConnection;

    private AccountLinkagePolicyDTO accountLinkagePolicy;

    private IdentityMappingPolicyDTO identityMappingPolicy;

    // RFU
    private AuthenticationContractDTO authenticationContract;

    // RFU
    private AuthenticationMechanismDTO authenticationMechanism;

    private boolean signAuthenticationRequests;

    private boolean wantAssertionSigned;

    private boolean signRequests;

    private boolean wantSignedRequests;

    @Override
    public ProviderRoleDTO getRole() {
        return ProviderRoleDTO.SSOServiceProvider;
    }

    @Override
    public void setRole(ProviderRoleDTO role) {
//        throw new UnsupportedOperationException("Cannot change provider role");
    }

    public ActivationDTO getActivation() {
        return activation;
    }

    public void setActivation(ActivationDTO activation) {
        this.activation = activation;
    }

    public ServiceConnectionDTO getServiceConnection() {
        return serviceConnection;
    }

    public void setServiceConnection(ServiceConnectionDTO serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    public AccountLinkagePolicyDTO getAccountLinkagePolicy() {
        return accountLinkagePolicy;
    }

    public void setAccountLinkagePolicy(AccountLinkagePolicyDTO accountLinkagePolicy) {
        this.accountLinkagePolicy = accountLinkagePolicy;
    }

    public IdentityMappingPolicyDTO getIdentityMappingPolicy() {
        return identityMappingPolicy;
    }

    public void setIdentityMappingPolicy(IdentityMappingPolicyDTO identityMappingPolicy) {
        this.identityMappingPolicy = identityMappingPolicy;
    }

    public AuthenticationContractDTO getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContractDTO authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public AuthenticationMechanismDTO getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanismDTO authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
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
}
