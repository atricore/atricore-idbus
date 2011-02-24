package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

public class JDOGroupAttributeDefinition implements Serializable {

    private static final long serialVersionUID = 9063299441686954647L;
    
    private Long id;

    private String name;

    private String description;

    private JDOAttributeType type;

    private Boolean required;

    private Boolean multivalued;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getMultivalued() {
        return multivalued;
    }

    public void setMultivalued(Boolean multivalued) {
        this.multivalued = multivalued;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public JDOAttributeType getType() {
        return type;
    }

    public void setType(JDOAttributeType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOGroupAttributeDefinition)) return false;

        JDOGroupAttributeDefinition that = (JDOGroupAttributeDefinition) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
