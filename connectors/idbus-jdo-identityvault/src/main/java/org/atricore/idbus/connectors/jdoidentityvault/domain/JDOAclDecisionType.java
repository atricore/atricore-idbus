package org.atricore.idbus.connectors.jdoidentityvault.domain;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public enum JDOAclDecisionType {

    ALLOW,
    DENY;

    public String value() {
        return name();
    }

    public static JDOAclDecisionType fromValue(String v) {
        return valueOf(v);
    }
}
