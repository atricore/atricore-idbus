package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public enum IdentityMappingTypeDTO {

    LOCAL("Use Ours"),
    REMOTE("Use Theirs"),
    MERGED("Aggregate"),
    CUSTOM("Custom");

    private String displayName;

    private IdentityMappingTypeDTO(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
