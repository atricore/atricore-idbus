package org.atricore.idbus.kernel.main.provisioning.domain;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum AttributeType {

    STRING,
    INT,
    DATE,
    EMAIL,
    URL;
    // TODO : Others ? (i.e. certificate, etc.)

    public String value() {
        return name();
    }

    public static AttributeType fromValue(String v) {
        return valueOf(v);
    }
}
