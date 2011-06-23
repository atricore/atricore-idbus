package com.atricore.idbus.console.services.dto;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum SubjectNameIDPolicyTypeDTO {
    EMAIL("Email Address"),
    PRINCIPAL("User Principal"),
    CUSTOM("Custom");

    private String displayName;

    SubjectNameIDPolicyTypeDTO(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
