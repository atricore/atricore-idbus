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

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LdapIdentitySourceDTO extends IdentitySourceDTO {

    private String initialContextFactory;
    private String providerUrl;
    private String securityPrincipal;
    private String securityCredential;
    private String securityAuthentication;
    private String ldapSearchScope;
    private String usersCtxDN;
    private String principalUidAttributeID;
    private String roleMatchingMode;
    private String uidAttributeID;
    private String rolesCtxDN;
    private String roleAttributeID;
    private String credentialQueryString;
    private String updateableCredentialAttribute;
    private String userPropertiesQueryString;
    private static final long serialVersionUID = 6035914548009890586L;

    public String getInitialContextFactory() {
        return initialContextFactory;
    }

    public void setInitialContextFactory(String initialContextFactory) {
        this.initialContextFactory = initialContextFactory;
    }

    public String getProviderUrl() {
        return providerUrl;
    }

    public void setProviderUrl(String providerUrl) {
        this.providerUrl = providerUrl;
    }

    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public void setSecurityCredential(String securityCredential) {
        this.securityCredential = securityCredential;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }

    public String getLdapSearchScope() {
        return ldapSearchScope;
    }

    public void setLdapSearchScope(String ldapSearchScope) {
        this.ldapSearchScope = ldapSearchScope;
    }

    public String getUsersCtxDN() {
        return usersCtxDN;
    }

    public void setUsersCtxDN(String usersCtxDN) {
        this.usersCtxDN = usersCtxDN;
    }

    public String getPrincipalUidAttributeID() {
        return principalUidAttributeID;
    }

    public void setPrincipalUidAttributeID(String principalUidAttributeID) {
        this.principalUidAttributeID = principalUidAttributeID;
    }

    public String getRoleMatchingMode() {
        return roleMatchingMode;
    }

    public void setRoleMatchingMode(String roleMatchingMode) {
        this.roleMatchingMode = roleMatchingMode;
    }

    public String getUidAttributeID() {
        return uidAttributeID;
    }

    public void setUidAttributeID(String uidAttributeID) {
        this.uidAttributeID = uidAttributeID;
    }

    public String getRolesCtxDN() {
        return rolesCtxDN;
    }

    public void setRolesCtxDN(String rolesCtxDN) {
        this.rolesCtxDN = rolesCtxDN;
    }

    public String getRoleAttributeID() {
        return roleAttributeID;
    }

    public void setRoleAttributeID(String roleAttributeID) {
        this.roleAttributeID = roleAttributeID;
    }

    public String getCredentialQueryString() {
        return credentialQueryString;
    }

    public void setCredentialQueryString(String credentialQueryString) {
        this.credentialQueryString = credentialQueryString;
    }

    public String getUpdateableCredentialAttribute() {
        return updateableCredentialAttribute;
    }

    public void setUpdateableCredentialAttribute(String updateableCredentialAttribute) {
        this.updateableCredentialAttribute = updateableCredentialAttribute;
    }

    public String getUserPropertiesQueryString() {
        return userPropertiesQueryString;
    }

    public void setUserPropertiesQueryString(String userPropertiesQueryString) {
        this.userPropertiesQueryString = userPropertiesQueryString;
    }
}
