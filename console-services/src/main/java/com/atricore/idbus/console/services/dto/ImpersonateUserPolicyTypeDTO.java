package com.atricore.idbus.console.services.dto;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum ImpersonateUserPolicyTypeDTO {

   DISABLED("Disabled"),
    CUSTOM("Custom");

    private String description;

    ImpersonateUserPolicyTypeDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
