package org.atricore.idbus.connectors.jdoidentityvault.domain;

/**
 * Created by sgonzalez.
 */
public enum JDOAttributePermission {

    NONE,
    READ,
    WRITE;

    private static final long serialVersionUID = 1233260808257897291L;

    public String value() {
        return name();
    }

    public static JDOAttributePermission fromValue(String v) {
        return valueOf(v);
    }

}
