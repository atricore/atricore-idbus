package org.atricore.idbus.capabilities.sso.main.binding;

import java.io.Serializable;

/**
 * SAML Artifact binding message wrapper, allows to store additional information.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SamlMessageWrapper implements Serializable {

    private String type;

    private Object msg;


    public SamlMessageWrapper(String type, Object msg) {
        this.type = type;
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public Object  getMsg() {
        return msg;
    }
}
