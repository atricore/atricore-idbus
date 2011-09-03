package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum ImpersonateUserPolicyType {

    DISABLED("Disabled"),
    CUSTOM("Custom");

    private String displayName;

    ImpersonateUserPolicyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
