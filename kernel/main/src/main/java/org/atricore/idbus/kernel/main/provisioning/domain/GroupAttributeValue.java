package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class GroupAttributeValue implements Serializable {

    private static final long serialVersionUID = 4595183226527197264L;

    private String id;

    private String name;

    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupAttributeValue)) return false;

        GroupAttributeValue that = (GroupAttributeValue) o;

        if (id != null)
            return id.equals(that.id);

        return false;
    }
}
