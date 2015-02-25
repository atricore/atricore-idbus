package org.atricore.idbus.capabilities.openidconnect.main.common;

import org.atricore.idbus.capabilities.sso.support.metadata.SSOMetadataConstants;

import javax.xml.namespace.QName;

/**
 * Created by sgonzalez on 3/11/14.
 */
public enum OpenIDConnectService {

    SPInitiatedSingleSignOnServiceProxy(SSOMetadataConstants.SPInitiatedSingleSignOnServiceProxy_QNAME),

    GoogleAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.GoogleAuthzTokenConsumerService_QNAME),

    FacebookAuthzTokenConsumerServiceProxy(OpenIDConnectConstants.FacebookAuthzTokenConsumerService_QNAME);

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

