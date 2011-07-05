package com.atricore.idbus.console.services.dto;

public enum AccountLinkEmitterTypeDTO {

    EMAIL("Email"),
    UID("UID"),
    ONE_TO_ONE("One To One"),
    CUSTOM("Custom");

    private String displayName;

    private AccountLinkEmitterTypeDTO(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
