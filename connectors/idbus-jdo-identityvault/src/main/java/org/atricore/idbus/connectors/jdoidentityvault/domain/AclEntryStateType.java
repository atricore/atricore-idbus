package org.atricore.idbus.connectors.jdoidentityvault.domain;

public enum AclEntryStateType {

    PENDING,
    APPROVED;

    public String value() {
        return name();
    }

    public static AclEntryStateType fromValue(String v) {
        return valueOf(v);
    }
}
