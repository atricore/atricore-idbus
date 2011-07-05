package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SubjectNameIdentifierPolicy implements Serializable {

    private static final long serialVersionUID = -765936176372645861L;

    private String id;

    private String name;

    private String descriptionKey;

    private SubjectNameIDPolicyType type;

    private String subjectAttribute;

    public SubjectNameIdentifierPolicy() {

    }

    public SubjectNameIdentifierPolicy(String id, String name, String descriptionKey, SubjectNameIDPolicyType type) {
        this.id = id;
        this.name = name;
        this.descriptionKey = descriptionKey;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSubjectAttribute() {
        return subjectAttribute;
    }

    public void setSubjectAttribute(String subjectAttribute) {
        this.subjectAttribute = subjectAttribute;
    }

    public SubjectNameIDPolicyType getType() {
        return type;
    }

    public void setType(SubjectNameIDPolicyType type) {
        this.type = type;
    }
}
