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

import java.util.Set;

public class IdentityProvider extends FederatedProvider {

	private static final long serialVersionUID = 141137856095909986L;

    private boolean wantAuthnRequestsSigned;

    private boolean signRequests;

    private boolean wantSignedRequests;

    private boolean ignoreRequestedNameIDPolicy = true;

    private int ssoSessionTimeout = 30; //

    private String dashboardUrl;

    // Do we need something abstract, not bound to oauth2 ?
    private String oauth2ClientsConfig;

    private String oauth2Key;

    private boolean oauth2Enabled;

    private boolean openIdEnabled;

    private boolean dominoEnabled;

    private String userDashboardBranding;

    private boolean identityConfirmationEnabled;

    private Extension identityConfirmationPolicy;

    private String identityConfirmationOAuth2ClientId;

    private String identityConfirmationOAuth2ClientSecret;

    private boolean externallyHostedIdentityConfirmationTokenService;

    private String identityConfirmationOAuth2AuthorizationServerEndpoint;

    // USERNAME, EMAIL, TRANSIENT, PERSISTENT, X509 Principal Name, Windows DC Principal
    private SubjectNameIdentifierPolicy subjectNameIDPolicy;

    // RFU
    private AttributeProfile attributeProfile;

    private Set<AuthenticationMechanism> authenticationMechanisms;

    // RFU
    private AuthenticationContract authenticationContract;

    // RFU
    private AuthenticationAssertionEmissionPolicy emissionPolicy;

    // RFU
    //TODO check whether LocalProvider will have bindings or IdentityProvider
//    private Set<Binding> activeBindings;

    // RFU
    //TODO check whether LocalProvider will have profiles or IdentityProvider    
//    private Set<Profile> activeProfiles;

    //private DelegatedAuthentication delegatedAuthentication;
    private Set<DelegatedAuthentication> delegatedAuthentications;

    private int messageTtl;
    
    private int messageTtlTolerance;

    @Override
    public ProviderRole getRole() {
        return ProviderRole.SSOIdentityProvider;
    }

    @Override
    public void setRole(ProviderRole role) {
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

    public AttributeProfile getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfile attributeProfile) {
        this.attributeProfile = attributeProfile;
    }


    public Set<AuthenticationMechanism> getAuthenticationMechanisms() {
        return authenticationMechanisms;
    }

    public void setAuthenticationMechanisms(Set<AuthenticationMechanism> authenticationMechanisms) {
        this.authenticationMechanisms = authenticationMechanisms;
    }

    public AuthenticationContract getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContract authenticationContract) {
        this.authenticationContract = authenticationContract;
    }

    public AuthenticationAssertionEmissionPolicy getEmissionPolicy() {
        return emissionPolicy;
    }

    public void setEmissionPolicy(AuthenticationAssertionEmissionPolicy emissionPolicy) {
        this.emissionPolicy = emissionPolicy;
    }

    public Set<DelegatedAuthentication> getDelegatedAuthentications() {
        return delegatedAuthentications;
    }

    public void setDelegatedAuthentications(Set<DelegatedAuthentication> delegatedAuthentications) {
        this.delegatedAuthentications = delegatedAuthentications;
    }

    public boolean isIgnoreRequestedNameIDPolicy() {
        return ignoreRequestedNameIDPolicy;
    }

    public void setIgnoreRequestedNameIDPolicy(boolean ignoreRequestedNameIDPolicy) {
        this.ignoreRequestedNameIDPolicy = ignoreRequestedNameIDPolicy;
    }

    public SubjectNameIdentifierPolicy getSubjectNameIDPolicy() {
        return subjectNameIDPolicy;
    }

    public void setSubjectNameIDPolicy(SubjectNameIdentifierPolicy subjectNameIDPolicy) {
        this.subjectNameIDPolicy = subjectNameIDPolicy;
    }

    public String getOauth2ClientsConfig() {
        return oauth2ClientsConfig;
    }

    public void setOauth2ClientsConfig(String oauth2ClientsConfig) {
        this.oauth2ClientsConfig = oauth2ClientsConfig;
    }

    public String getOauth2Key() {
        return oauth2Key;
    }

    public void setOauth2Key(String oauth2Key) {
        this.oauth2Key = oauth2Key;
    }

    public boolean isOauth2Enabled() {
        return oauth2Enabled;
    }

    public void setOauth2Enabled(boolean oauth2Enabled) {
        this.oauth2Enabled = oauth2Enabled;
    }

    public boolean isOpenIdEnabled() {
        return openIdEnabled;
    }

    public void setOpenIdEnabled(boolean openIdEnabled) {
        this.openIdEnabled = openIdEnabled;
    }

    public boolean isDominoEnabled() {
        return dominoEnabled;
    }

    public void setDominoEnabled(boolean dominoEnabled) {
        this.dominoEnabled = dominoEnabled;
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

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;

    }

    public String getUserDashboardBranding() {
        return userDashboardBranding;
    }

    public void setUserDashboardBranding(String userDashboardBranding) {
        this.userDashboardBranding = userDashboardBranding;
    }

    public boolean isIdentityConfirmationEnabled() {
        return identityConfirmationEnabled;
    }

    public void setIdentityConfirmationEnabled(boolean identityConfirmationEnabled) {
        this.identityConfirmationEnabled = identityConfirmationEnabled;
    }

    public Extension getIdentityConfirmationPolicy() {
        return identityConfirmationPolicy;
    }

    public void setIdentityConfirmationPolicy(Extension identityConfirmationPolicy) {
        this.identityConfirmationPolicy = identityConfirmationPolicy;
    }

    public String getIdentityConfirmationOAuth2ClientId() {
        return identityConfirmationOAuth2ClientId;
    }

    public void setIdentityConfirmationOAuth2ClientId(String identityConfirmationOAuth2ClientId) {
        this.identityConfirmationOAuth2ClientId = identityConfirmationOAuth2ClientId;
    }

    public String getIdentityConfirmationOAuth2ClientSecret() {
        return identityConfirmationOAuth2ClientSecret;
    }

    public void setIdentityConfirmationOAuth2ClientSecret(String identityConfirmationOAuth2ClientSecret) {
        this.identityConfirmationOAuth2ClientSecret = identityConfirmationOAuth2ClientSecret;
    }

    public boolean isExternallyHostedIdentityConfirmationTokenService() {
        return externallyHostedIdentityConfirmationTokenService;
    }

    public void setExternallyHostedIdentityConfirmationTokenService(boolean externallyHostedIdentityConfirmationTokenService) {
        this.externallyHostedIdentityConfirmationTokenService = externallyHostedIdentityConfirmationTokenService;
    }

    public String getIdentityConfirmationOAuth2AuthorizationServerEndpoint() {
        return identityConfirmationOAuth2AuthorizationServerEndpoint;
    }

    public void setIdentityConfirmationOAuth2AuthorizationServerEndpoint(String identityConfirmationOAuth2AuthorizationServerEndpoint) {
        this.identityConfirmationOAuth2AuthorizationServerEndpoint = identityConfirmationOAuth2AuthorizationServerEndpoint;
    }

}
