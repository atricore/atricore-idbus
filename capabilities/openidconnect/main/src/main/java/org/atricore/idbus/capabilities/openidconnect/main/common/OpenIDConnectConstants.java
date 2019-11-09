package org.atricore.idbus.capabilities.openidconnect.main.common;

import com.nimbusds.oauth2.sdk.GrantType;

import javax.xml.namespace.QName;

/**
 * Created by sgonzalez on 3/11/14.
 */
public interface OpenIDConnectConstants {

    QName CLIENT_ID = new QName("urn:org:atricore:idbus:openidconnect:op", "clientID");

    // Open ID Connect
    QName AuthorizationGrant_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "AuthorizationGrant");

    // Open ID Connect Identity Provider Services
    QName AuthorizationService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "AuthorizationService");

    QName TwitterAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "TwitterAuthzTokenConsumerService");

    QName AuthzCodeProviderService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "AuthzCodeProviderService");
    
    QName TokenService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "TokenService");

    QName UserInfoService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "UserInfoService");

    QName RPTokenService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "RPTokenService");

    QName RPUserInfoService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "RPTUserInfoService");

    QName RPInitLogoutService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "LogoutService");

    QName MetadataService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "MetadataService");

    QName JWKService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "JWK");

    QName SSOAssertionConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "AssertionConsumerService");

    QName SSOSingleSignOnService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "SSOSingleSignOnService");

    QName SSOSingleLogoutService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:op", "SSOSingleLogoutService");

    // Open ID Connect Relaying Party Services
    QName AuthorizationConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:rp", "AuthorizationConsumerService");

    QName TokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:rp", "TokenConsumerService");

    // Open ID Connect Identity Provider Proxy Services

    QName GoogleAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "GoogleAuthzTokenConsumerService");

    QName FacebookAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "FacebookAuthzTokenConsumerService");

    QName LinkedInAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "LinkedInAuthzTokenConsumerService");

    QName WeChatAuthzTokenConsumerService_QNAME = new QName("urn:org:atricore:idbus:openidconnect:metadata", "WeChatAuthzTokenConsumerService");

    QName IDPSSODescriptor_QNAME = new QName("urn:openidconnect:1.0", "ProviderDescriptor");

    QName SPSSODescriptor_QNAME = new QName("urn:openidconnect:1.0", "RelayingPartyDesriptor");

    String OIDC_EXT_NAMESPACE = "urn:org:atricore:idbus:openidconnect:ext";

    String SEC_CTX_AUTHZ_CODE_KEY = "authorization_code";

    String SEC_CTX_REFRESH_TOKEN_KEY = "refresh_token";

    String SEC_CTX_ACCESS_TOKEN_KEY = "access_token";

    String AUTHN_CTX_KEY = "urn:org:atricore:idbus:capabilities:openidconnect:authnCtx";

    /**
     * JWT bearer, as defined in draft-ietf-oauth-jwt-bearer-10. Explicit
     * client authentication is optional.
     */
    GrantType JWT_BEARER_PWD = new GrantType("urn:ietf:params:oauth:grant-type:jwt-bearer-pwd");

}
