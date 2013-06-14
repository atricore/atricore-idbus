package org.atricore.idbus.kernel.main.provisioning.domain;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
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
