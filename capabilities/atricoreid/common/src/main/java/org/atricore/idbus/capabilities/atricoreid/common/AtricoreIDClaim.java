package org.atricore.idbus.capabilities.atricoreid.common;

import java.io.StringWriter;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDClaim {

    private String type;

    private String value;

    public AtricoreIDClaim(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public AtricoreIDClaim() {
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
}
