package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public class DirectoryAuthenticationService extends AuthenticationService {

    private static final long serialVersionUID = 2638724055199752101L;

    private String initialContextFactory;
    private String providerUrl;
    private boolean performDnSearch;
    private String passwordPolicy;
    private String principalUidAttributeID;
    private String usersCtxDN;
    private String securityPrincipal;
    private String securityCredential;
    private String ldapSearchScope;
    private String securityAuthentication;


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

    public boolean isPerformDnSearch() {
        return performDnSearch;
    }

    public void setPerformDnSearch(boolean performDnSearch) {
        this.performDnSearch = performDnSearch;
    }

    public String getPasswordPolicy() {
        return passwordPolicy;
    }

    public void setPasswordPolicy(String passwordPolicy) {
        this.passwordPolicy = passwordPolicy;
    }

    public String getSecurityAuthentication() {
        return securityAuthentication;
    }

    public void setSecurityAuthentication(String securityAuthentication) {
        this.securityAuthentication = securityAuthentication;
    }

    public String getPrincipalUidAttributeID() {
        return principalUidAttributeID;
    }

    public void setPrincipalUidAttributeID(String principalUidAttributeID) {
        this.principalUidAttributeID = principalUidAttributeID;
    }

    public String getUsersCtxDN() {
        return usersCtxDN;
    }

    public void setUsersCtxDN(String usersCtxDN) {
        this.usersCtxDN = usersCtxDN;
    }

    public String getSecurityCredential() {
        return securityCredential;
    }

    public void setSecurityCredential(String securityCredential) {
        this.securityCredential = securityCredential;
    }

    public String getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(String securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public String getLdapSearchScope() {
        return ldapSearchScope;
    }

    public void setLdapSearchScope(String ldapSearchScope) {
        this.ldapSearchScope = ldapSearchScope;
    }
}
