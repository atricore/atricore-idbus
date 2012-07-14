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

public class IdentityProviderChannel extends FederatedChannel {

	private static final long serialVersionUID = 8276649486690667445L;

    private boolean preferred;

    private AccountLinkagePolicy accountLinkagePolicy;

    private IdentityMappingPolicy identityMappingPolicy;

    // RFU
    private AuthenticationContract authenticationContract;

    // RFU
    private AuthenticationMechanism authenticationMechanism;

    private boolean signAuthenticationRequests;

    private boolean wantAssertionSigned;
    
    private int messageTtl;
    
    private int messageTtlTolerance;

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

    public AuthenticationMechanism getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanism authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
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
