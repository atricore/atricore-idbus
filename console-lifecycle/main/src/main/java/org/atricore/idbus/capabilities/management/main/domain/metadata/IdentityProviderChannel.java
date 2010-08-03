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

package org.atricore.idbus.capabilities.management.main.domain.metadata;

public class IdentityProviderChannel extends Channel {

	private static final long serialVersionUID = 8276649486690667445L;

    private IdentityVault identityVault;

    // Overrides identityVault user information lookup
    private UserInformationLookup userInformationLookup;

    private AccountLinkagePolicy accountLinkagePolicy;

    private AuthenticationContract authenticationContract;

    private AuthenticationMechanism authenticationMechanism;
    

    public IdentityVault getIdentityVault() {
        return identityVault;
    }

    public void setIdentityVault(IdentityVault identityVault) {
        this.identityVault = identityVault;
    }

    public UserInformationLookup getUserInformationLookup() {
        return userInformationLookup;
    }

    public void setUserInformationLookup(UserInformationLookup userInformationLookup) {
        this.userInformationLookup = userInformationLookup;
    }

    public AccountLinkagePolicy getAccountLinkagePolicy() {
        return accountLinkagePolicy;
    }

    public void setAccountLinkagePolicy(AccountLinkagePolicy accountLinkagePolicy) {
        this.accountLinkagePolicy = accountLinkagePolicy;
    }

    public AuthenticationContract getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContract authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public AuthenticationMechanism getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanism authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }
}
