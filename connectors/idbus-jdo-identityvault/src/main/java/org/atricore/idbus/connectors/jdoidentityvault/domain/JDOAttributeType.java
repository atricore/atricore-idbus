package org.atricore.idbus.connectors.jdoidentityvault.domain;

public enum JDOAttributeType {

    STRING,
    INT,
    DATE,
    EMAIL,
    URL;
    // TODO : Others ? (i.e. certificate, etc.)

    public String value() {
        return name();
    }

    public static JDOAttributeType fromValue(String v) {
        return valueOf(v);
    }
}
