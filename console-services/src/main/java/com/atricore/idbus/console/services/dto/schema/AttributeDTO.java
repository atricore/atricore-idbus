package com.atricore.idbus.console.services.dto.schema;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AttributeDTO {

    private int id;

    private String entity; // user or group

    private String name;

    private TypeDTOEnum type;

    private boolean required;

    private boolean multivalued;

    public AttributeDTO(int id, String entity, String name, TypeDTOEnum type, boolean required, boolean multivalued) {
        this.id = id;
        this.entity = entity;
        this.name = name;
        this.type = type;
        this.required = required;
        this.multivalued = multivalued;
    }

    public int getId() {
        return id;
    }

    public String getEntity() {
        return entity;
    }

    public String getName() {
        return name;
    }

    public TypeDTOEnum getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isMultivalued() {
        return multivalued;
    }
}
