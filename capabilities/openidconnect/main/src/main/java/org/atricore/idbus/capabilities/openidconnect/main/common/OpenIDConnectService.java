package org.atricore.idbus.capabilities.openidconnect.main.common;

import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;

import javax.xml.namespace.QName;

/**
 * OpenID Connect Service Types
 */
public enum OpenIDConnectService {

    // Provider Related Services

    SSOSingleSignOnService(OpenIDConnectConstants.SSOSingleSignOnService_QNAME),

    SSOSingleLogoutService(OpenIDConnectConstants.SSOSingleLogoutService_QNAME),

    PayloadResolutionService(OpenIDConnectConstants.PayloadResolutionService_QNAME),

    AuthorizationService(OpenIDConnectConstants.AuthorizationService_QNAME),

    SSOAssertionConsumerService(OpenIDConnectConstants.SSOAssertionConsumerService_QNAME),

    TokenService(OpenIDConnectConstants.TokenService_QNAME),

    UserInfoService(OpenIDConnectConstants.UserInfoService_QNAME),

    RPTokenService(OpenIDConnectConstants.RPTokenService_QNAME),

    RPUserInfoService(OpenIDConnectConstants.RPUserInfoService_QNAME),

    RPInitLogoutService(OpenIDConnectConstants.RPInitLogoutService_QNAME),

    RPCheckSessionIFrameService(OpenIDConnectConstants.RPCheckSessionIFrameService_QNAME),

    OPStateService(OpenIDConnectConstants.OPStateService_QNAME),

    MetadataService(OpenIDConnectConstants.MetadataService_QNAME),

    JWKService(OpenIDConnectConstants.JWKService_QNAME),

    // Relaying Party Services
    AuthorizationConsumerService(OpenIDConnectConstants.AuthorizationConsumerService_QNAME),

    // Proxy Related Services

    OidcAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.OidcAuthzTokenConsumerService_QNAME),

    SPInitiatedSingleSignOnServiceProxy(SSOMetadataConstants.SPInitiatedSingleSignOnServiceProxy_QNAME),

    SPInitiatedAuhnServiceProxy(SSOMetadataConstants.SPInitiatedAuthnServiceProxy_QNAME),

    ExtOpAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.ExtOpAuthzTokenConsumerService_QNAME),

    AzureAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.AzureAuthzTokenConsumerService_QNAME),

    GoogleAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.GoogleAuthzTokenConsumerService_QNAME),

    FacebookAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.FacebookAuthzTokenConsumerService_QNAME),

    TwitterAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.TwitterAuthzTokenConsumerService_QNAME),

    LinkedInAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.LinkedInAuthzTokenConsumerService_QNAME),

    WeChatAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.WeChatAuthzTokenConsumerService_QNAME);

    private QName qname;


    OpenIDConnectService(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    OpenIDConnectService(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }


    public static OpenIDConnectService asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";

        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static OpenIDConnectService asEnum(QName qname) {
        for (OpenIDConnectService et : values()) {
            if (et.getQname().equals(qname))
                return et;
        }

        throw new IllegalArgumentException("Invalid endpoint type: " + qname);
    }

    @Override
    public String toString() {
        return qname.toString();
    }
}

