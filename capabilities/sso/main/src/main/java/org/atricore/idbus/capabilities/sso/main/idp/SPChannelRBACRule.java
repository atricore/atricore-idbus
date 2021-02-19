package org.atricore.idbus.capabilities.sso.main.idp;

import java.util.ArrayList;
import java.util.List;

public class SPChannelRBACRule implements java.io.Serializable {

    public static final int ROLES_MATCH_MODE_ANY = 0;

    public static final int ROLES_MATCH_MODE_ALL = 1;

    // SP alias
    private String alias;

    private int requiredRolesMatchMode = ROLES_MATCH_MODE_ANY;
    private List<String> requiredRoles = new ArrayList<String>();

    private int restrictedRolesMatchMode = ROLES_MATCH_MODE_ANY;
    private List<String> restrictedRoles = new ArrayList<String>();

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getRequiredRolesMatchMode() {
        return requiredRolesMatchMode;
    }

    public void setRequiredRolesMatchMode(int requiredRolesMatchMode) {
        this.requiredRolesMatchMode = requiredRolesMatchMode;
    }

    public List<String> getRequiredRoles() {
        return requiredRoles;
    }

    public void setRequiredRoles(List<String> requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    public int getRestrictedRolesMatchMode() {
        return restrictedRolesMatchMode;
    }

    public void setRestrictedRolesMatchMode(int restrictedRolesMatchMode) {
        this.restrictedRolesMatchMode = restrictedRolesMatchMode;
    }

    public List<String> getRestrictedRoles() {
        return restrictedRoles;
    }

    public void setRestrictedRoles(List<String> restrictedRoles) {
        this.restrictedRoles = restrictedRoles;
    }

}
