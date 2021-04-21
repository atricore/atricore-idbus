package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserAttributeDefinition implements Serializable {

    private static final long serialVersionUID = -2777877988862110225L;
    
    private String id;

    private String name;

    private String description;

    private AttributeType type;

    private AttributePermission userPermission;

    private AttributePermission adminPermission;

    private boolean required;

    private boolean multivalued;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
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

    public AttributePermission getUserPermission() {
        return userPermission;
    }

    public void setUserPermission(AttributePermission userPermission) {
        this.userPermission = userPermission;
    }

    public AttributePermission getAdminPermission() {
        return adminPermission;
    }

    public void setAdminPermission(AttributePermission adminPermission) {
        this.adminPermission = adminPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAttributeDefinition)) return false;

        UserAttributeDefinition that = (UserAttributeDefinition) o;

        if (id != null)
            return id.equals(that.id);

        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}
