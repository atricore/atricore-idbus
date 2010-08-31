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

public class IdentityProviderDTO extends LocalProviderDTO {

	private static final long serialVersionUID = 141137856095909986L;

    private boolean signAuthenticationAssertions;
    private boolean encryptAuthenticationAssertions;

    // RFU
    private AttributeProfileDTO attributeProfile;

    // RFU
    private AuthenticationMechanismDTO authenticationMechanism;

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

    @Override
    public ProviderRoleDTO getRole() {
        return ProviderRoleDTO.SSOIdentityProvider;
    }

    @Override
    public void setRole(ProviderRoleDTO role) {
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
}
