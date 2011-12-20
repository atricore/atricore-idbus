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

import java.util.HashSet;
import java.util.Set;

public class IdentityProviderDTO extends FederatedProviderDTO {

	private static final long serialVersionUID = 141137856095909986L;

    private boolean wantAuthnRequestsSigned;

    private boolean signRequests;

    private boolean wantSignedRequests;

    private boolean ignoreRequestedNameIDPolicy = true;

    private int ssoSessionTimeout;

    private String oauth2ClientsConfig;

    // USERNAME, EMAIL, TRANSIENT, PERSISTENT, X509 Principal Name, Windows DC Principal
    private SubjectNameIdentifierPolicyDTO subjectNameIDPolicy;

    // RFU
    private AttributeProfileDTO attributeProfile;

    // RFU
    private Set<AuthenticationMechanismDTO> authenticationMechanisms;

    // RFU
    private AuthenticationContractDTO authenticationContract;

    // RFU
    private AuthenticationAssertionEmissionPolicyDTO emissionPolicy;

    // RFU
    //TODO check whether LocalProvider will have bindings or IdentityProvider
//    private Set<BindingDTO> activeBindings;

    // RFU
    //TODO check whether LocalProvider will have profiles or IdentityProvider
//    private Set<ProfileDTO> activeProfiles;

    private DelegatedAuthenticationDTO delegatedAuthentication;

    @Override
    public ProviderRoleDTO getRole() {
        return ProviderRoleDTO.SSOIdentityProvider;
    }

    @Override
    public void setRole(ProviderRoleDTO role) {
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

    public int getSsoSessionTimeout() {
        return ssoSessionTimeout;
    }

    public void setSsoSessionTimeout(int ssoSessionTimeout) {
        this.ssoSessionTimeout = ssoSessionTimeout;
    }

    public AttributeProfileDTO getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfileDTO attributeProfile) {
        this.attributeProfile = attributeProfile;
    }

    public AuthenticationContractDTO getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContractDTO authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public Set<AuthenticationMechanismDTO> getAuthenticationMechanisms() {
        if(authenticationMechanisms == null){
            authenticationMechanisms = new HashSet<AuthenticationMechanismDTO>();
        }
        return authenticationMechanisms;
    }

    public void setAuthenticationMechanisms(Set<AuthenticationMechanismDTO> authenticationMechanisms) {
        this.authenticationMechanisms = authenticationMechanisms;
    }

    public AuthenticationAssertionEmissionPolicyDTO getEmissionPolicy() {
        return emissionPolicy;
    }

    public void setEmissionPolicy(AuthenticationAssertionEmissionPolicyDTO emissionPolicy) {
        this.emissionPolicy = emissionPolicy;
    }

    public DelegatedAuthenticationDTO getDelegatedAuthentication() {
        return delegatedAuthentication;
    }

    public void setDelegatedAuthentication(DelegatedAuthenticationDTO delegatedAuthentication) {
        this.delegatedAuthentication = delegatedAuthentication;
    }

    public boolean isIgnoreRequestedNameIDPolicy() {
        return ignoreRequestedNameIDPolicy;
    }

    public void setIgnoreRequestedNameIDPolicy(boolean ignoreRequestedNameIDPolicy) {
        this.ignoreRequestedNameIDPolicy = ignoreRequestedNameIDPolicy;
    }

    public SubjectNameIdentifierPolicyDTO getSubjectNameIDPolicy() {
        return subjectNameIDPolicy;
    }

    public void setSubjectNameIDPolicy(SubjectNameIdentifierPolicyDTO subjectNameIDPolicy) {
        this.subjectNameIDPolicy = subjectNameIDPolicy;
    }

    public String getOauth2ClientsConfig() {
        return oauth2ClientsConfig;
    }

    public void setOauth2ClientsConfig(String oauth2ClientsConfig) {
        this.oauth2ClientsConfig = oauth2ClientsConfig;
    }
}
