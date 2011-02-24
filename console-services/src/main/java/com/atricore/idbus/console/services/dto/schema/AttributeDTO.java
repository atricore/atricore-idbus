package com.atricore.idbus.console.services.dto.schema;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AttributeDTO {

    private int id;

    private String entity; // user or group

    private String name;

    private String description;

    private TypeDTOEnum type;

    private boolean required;

    private boolean multivalued;

    public AttributeDTO() {

    }

    public AttributeDTO(int id, String entity, String name, TypeDTOEnum type, boolean required, boolean multivalued) {
        this.id = id;
        this.entity = entity;
        this.name = name;
        this.type = type;
        this.required = required;
        this.multivalued = multivalued;
    }

    public AttributeDTO(int id, String entity, String name, String description, TypeDTOEnum type, boolean required, boolean multivalued) {
        this.id = id;
        this.entity = entity;
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.multivalued = multivalued;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeDTOEnum getType() {
        return type;
    }

    public void setType(TypeDTOEnum type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isMultivalued() {
        return multivalued;
    }

    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }
}
