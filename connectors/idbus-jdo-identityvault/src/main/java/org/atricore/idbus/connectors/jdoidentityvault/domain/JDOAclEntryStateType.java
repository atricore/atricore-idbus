package org.atricore.idbus.connectors.jdoidentityvault.domain;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public enum JDOAclEntryStateType {

    PENDING,
    APPROVED;

    public String value() {
        return name();
    }

    public static JDOAclEntryStateType fromValue(String v) {
        return valueOf(v);
    }
}
