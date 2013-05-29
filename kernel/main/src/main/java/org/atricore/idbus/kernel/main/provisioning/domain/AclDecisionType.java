package org.atricore.idbus.kernel.main.provisioning.domain;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
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
