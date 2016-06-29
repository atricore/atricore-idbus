package org.atricore.idbus.capabilities.oauth2.common;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum OAuth2Service {

    AuthorizationService(new QName(OAuth2Constants.OAUTH2_SERVICE_BASE_URI, "AuthorizationService")),

    SSOAssertionConsumerService(new QName(OAuth2Constants.SSO_SERVICE_BASE_URI, "AssertionConsumerService")),

    SSOSingleSignOnService(new QName(OAuth2Constants.SSO_SERVICE_BASE_URI, "SingleSignOnService")),

    SSOSingleLogoutService(new QName(OAuth2Constants.SSO_SERVICE_BASE_URI, "SingleLogoutService")),

    TokenService(new QName(OAuth2Constants.OAUTH2_SERVICE_BASE_URI, "TokenService")),

;

    private QName qname;

    OAuth2Service(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    OAuth2Service(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }

    public static OAuth2Service asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";
        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static OAuth2Service asEnum(QName qname) {
        for (OAuth2Service et : values()) {
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
