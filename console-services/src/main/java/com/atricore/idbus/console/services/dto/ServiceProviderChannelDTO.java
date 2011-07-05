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

public class ServiceProviderChannelDTO extends FederatedChannelDTO {

	
	private static final long serialVersionUID = 6144244229951343612L;

    // RFU
    private AttributeProfileDTO attributeProfile;

    // RFU
    private AuthenticationMechanismDTO authenticationMechanism;

    // RFU
    private AuthenticationContractDTO authenticationContract;

    // RFU
    private AuthenticationAssertionEmissionPolicyDTO emissionPolicy;

    private boolean wantAuthnRequestsSigned;

    private boolean ignoreRequestedNameIDPolicy = true;

    // USERNAME, EMAIL, TRANSIENT, PERSISTENT, X509 Principal Name, Windows DC Principal
    private SubjectNameIdentifierPolicyDTO subjectNameIDPolicy;

    public AttributeProfileDTO getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfileDTO attributeProfile) {
        this.attributeProfile = attributeProfile;
    }

    public AuthenticationMechanismDTO getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanismDTO authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }

    public AuthenticationContractDTO getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContractDTO authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public AuthenticationAssertionEmissionPolicyDTO getEmissionPolicy() {
        return emissionPolicy;
    }

    public void setEmissionPolicy(AuthenticationAssertionEmissionPolicyDTO emissionPolicy) {
        this.emissionPolicy = emissionPolicy;
    }

    public boolean isWantAuthnRequestsSigned() {
        return wantAuthnRequestsSigned;
    }

    public void setWantAuthnRequestsSigned(boolean wantAuthnRequestsSigned) {
        this.wantAuthnRequestsSigned = wantAuthnRequestsSigned;
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
}
