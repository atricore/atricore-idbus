package org.atricore.idbus.kernel.main.provisioning.domain;

/**
 * Created by sgonzalez.
 */
public enum AttributePermission {

    NONE,
    READ,
    WRITE;

    public String value() {
        return name();
    }

    public static AttributePermission fromValue(String v) {
        return valueOf(v);
    }

}
