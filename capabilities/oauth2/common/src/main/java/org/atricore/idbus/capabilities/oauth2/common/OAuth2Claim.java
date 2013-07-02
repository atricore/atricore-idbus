package org.atricore.idbus.capabilities.oauth2.common;

import java.io.Serializable;
import java.io.StringWriter;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Claim  implements Serializable {

    private String type;

    private String value;

    private String attr;

    public OAuth2Claim(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public OAuth2Claim(String type, String value, String attr) {
        this.type = type;
        this.value = value;
        this.attr = attr;
    }


    public OAuth2Claim() {
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttribute() {
        return attr;
    }

    public void setAttribute(String attr) {
        this.attr = attr;
    }
}
