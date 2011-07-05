package com.atricore.idbus.console.services.dto;

public class DirectoryAuthenticationServiceDTO extends AuthenticationServiceDTO {

    private static final long serialVersionUID = -2637953445913433166L;

    private String initialContextFactory;
    private String providerUrl;
    private boolean performDnSearch;
    private String passwordPolicy;
    private String securityAuthentication;

    private String usersCtxDN;
    private String principalUidAttributeID;
    private String securityPrincipal;
    private String securityCredential;
    private String ldapSearchScope;


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

    public String getLdapSearchScope() {
        return ldapSearchScope;
    }

    public void setLdapSearchScope(String ldapSearchScope) {
        this.ldapSearchScope = ldapSearchScope;
    }
}
