package com.atricore.idbus.console.services.dto;

import org.atricore.idbus.capabilities.samlr2.support.core.NameIDFormat;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SubjectNameIdentifierPolicyDTO {

    private String name;

    private String descriptionKey;

    private SubjectNameIDPolicyTypeDTO type;

    public SubjectNameIdentifierPolicyDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public SubjectNameIDPolicyTypeDTO getType() {
        return type;
    }

    public void setType(SubjectNameIDPolicyTypeDTO type) {
        this.type = type;
    }
}