package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CustomNameIdentifierPolicy extends SubjectNameIdentifierPolicy {

    private String customNameIDBuilder;

    public CustomNameIdentifierPolicy(String id, String name, String descriptionKey, SubjectNameIDPolicyType type) {
        super(id, name, descriptionKey, type);
    }

    public CustomNameIdentifierPolicy() {
        super();
    }

    public String getCustomNameIDBuilder() {
        return customNameIDBuilder;
    }

    public void setCustomNameIDBuilder(String customNameIDBuilder) {
        this.customNameIDBuilder = customNameIDBuilder;
    }
}
