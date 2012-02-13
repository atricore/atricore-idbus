package org.atricore.idbus.capabilities.atricoreid.common;

import javax.management.relation.Role;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum AtricoreIDClaimType {

    USERID,
    UNKNOWN,
    ROLE;

    public static AtricoreIDClaimType asEnum(String value) {
        for (AtricoreIDClaimType et : values()) {
            if (et.toString().equals(value))
                return et;
        }
        throw new IllegalArgumentException("Invalid OAUTH2 Claim type : " + value);

    }
}
