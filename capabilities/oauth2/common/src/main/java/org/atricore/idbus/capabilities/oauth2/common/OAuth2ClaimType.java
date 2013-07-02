package org.atricore.idbus.capabilities.oauth2.common;

import javax.management.relation.Role;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum OAuth2ClaimType {

    USERID,
    ATTRIBUTE,
    UNKNOWN,
    ROLE;

    public static OAuth2ClaimType asEnum(String value) {
        for (OAuth2ClaimType et : values()) {
            if (et.toString().equals(value))
                return et;
        }
        throw new IllegalArgumentException("Invalid OAUTH2 Claim type : " + value);

    }
}
