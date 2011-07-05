package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public enum IdentityMappingType {

    LOCAL("Use Ours"),
    REMOTE("Use Theirs"),
    MERGED("Aggregate"),
    CUSTOM("Custom");

    private String displayName;

    private IdentityMappingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
