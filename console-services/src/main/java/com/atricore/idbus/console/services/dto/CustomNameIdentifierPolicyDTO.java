package com.atricore.idbus.console.services.dto;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class CustomNameIdentifierPolicyDTO extends SubjectNameIdentifierPolicyDTO {

    private String customNameIDBuilder;

    public CustomNameIdentifierPolicyDTO() {
        super();
    }

    public String getCustomNameIDBuilder() {
        return customNameIDBuilder;
    }

    public void setCustomNameIDBuilder(String customNameIDBuilder) {
        this.customNameIDBuilder = customNameIDBuilder;
    }
}
