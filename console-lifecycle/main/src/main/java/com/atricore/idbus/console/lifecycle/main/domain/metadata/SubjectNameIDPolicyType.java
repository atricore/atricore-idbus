package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum SubjectNameIDPolicyType {

    EMAIL("Email Address"),
    PRINCIPAL("User Principal"),
    CUSTOM("Custom");

    private String displayName;

    SubjectNameIDPolicyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
