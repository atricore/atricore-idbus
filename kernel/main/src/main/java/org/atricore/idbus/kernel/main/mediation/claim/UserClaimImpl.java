package org.atricore.idbus.kernel.main.mediation.claim;

import org.atricore.idbus.kernel.main.mediation.claim.UserClaim;

/**
 */
public class UserClaimImpl implements UserClaim {

    private String qualifier;
    private String name;
    private Object value;

    public UserClaimImpl(String qualifier, String name, Object value) {
        this.qualifier = qualifier;
        this.name = name;
        this.value = value;
    }

    public UserClaimImpl(String name, Object value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public String getQualifier() {
        return qualifier;
    }

    public Object getValue() {
        return value;
    }



    public String toString() {
        return "{"+qualifier+"}" + name + "=" + value;
    }
}
