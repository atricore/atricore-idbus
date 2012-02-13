package org.atricore.idbus.capabilities.atricoreid.common;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum AtricoreIDService {

    AuthorizationService(new QName(AtricoreIDConstants.OAUTH2_SERVICE_BASE_URI, "AuthorizationService")),

    TokenService(new QName(AtricoreIDConstants.OAUTH2_SERVICE_BASE_URI, "TokenService")),

;

    private QName qname;

    AtricoreIDService(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    AtricoreIDService(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }

    public static AtricoreIDService asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";
        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static AtricoreIDService asEnum(QName qname) {
        for (AtricoreIDService et : values()) {
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
