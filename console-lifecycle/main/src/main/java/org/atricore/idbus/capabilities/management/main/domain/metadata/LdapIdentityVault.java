package org.atricore.idbus.capabilities.management.main.domain.metadata;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LdapIdentityVault extends IdentityVault {

    private static final long serialVersionUID = 6035914548009890586L;

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
