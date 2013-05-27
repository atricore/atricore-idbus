package org.atricore.idbus.connectors.jdoidentityvault.domain;

public enum AclDecisionType {

    ALLOW,
    DENY;

    public String value() {
        return name();
    }

    public static AclDecisionType fromValue(String v) {
        return valueOf(v);
    }
}
