package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

public class JDOUserAttributeDefinition implements Serializable {

    private static final long serialVersionUID = 6502939705767222462L;
    
    private Long id;

    private String name;

    private String description;

    private JDOAttributeType type;

    private JDOAttributePermission userPermission;

    private JDOAttributePermission adminPermission;

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

    public JDOAttributePermission getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(JDOAttributePermission userPermission) {
        this.userPermission = userPermission;
    }

    public JDOAttributePermission getAdminPermission() {
        return adminPermission;
    }

    public void setAdminPermission(JDOAttributePermission adminPermission) {
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOUserAttributeDefinition)) return false;

        JDOUserAttributeDefinition that = (JDOUserAttributeDefinition) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
