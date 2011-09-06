package org.atricore.idbus.capabilities.sso.support.core;

import org.atricore.idbus.capabilities.sso.support.SAMLR11Constants;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public enum StatusCode11 {

    // Status codes
    TOP_SUCCESS
            ("Success", "Success"),
    TOP_REQUESTER
            ("Requester","The request could not be performed due to an error on the part of the requester"),
    TOP_RESPONDER
            ("Responder", "The request could not be performed due to an error on the part of the SAML responder or SAML authority"),
    TOP_VERSION_MISSMATCH
            ("VersionMismatch", "The SAML responder could not process the request because the version of the request message was incorrect."),
    ;

    // TODO : Add SAML 1.1 Status codes

    StatusCode11(String name, String description) {
        this.value = name;
        this.description = description;
        this.qname = new QName(SAMLR11Constants.SAML_PROTOCOL_NS, value);
    }

    private String value;
    private String description;
    private QName qname;

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public QName getQName() {
        return qname;
    }

    public static StatusCode11 asEnum(String a) {
        for (StatusCode11 ac : values()) {
            if (ac.getValue().equals(a))
                return ac;
        }

        throw new IllegalArgumentException("Invalid Status Code '" + a + "'");
    }

}
