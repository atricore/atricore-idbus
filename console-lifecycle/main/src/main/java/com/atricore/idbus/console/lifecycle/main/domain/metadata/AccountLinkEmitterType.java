package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum AccountLinkEmitterType {
    
    EMAIL("Email"),
    UID("UID"),
    ONE_TO_ONE("One To One"),
    CUSTOM("Custom");

    private String displayName;

    private AccountLinkEmitterType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
