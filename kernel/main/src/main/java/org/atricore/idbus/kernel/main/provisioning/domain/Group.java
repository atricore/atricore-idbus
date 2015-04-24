package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class Group implements Serializable {

    private static final long serialVersionUID = 4595183658527599864L;

    private String oid;
    private String name;
    private String description;

    private GroupAttributeValue[] attrs;
    
    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
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

    public GroupAttributeValue[] getAttrs() {
        return attrs;
    }

    public void setAttrs(GroupAttributeValue[] attrs) {
        this.attrs = attrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;

        Group that = (Group) o;

        if (oid != null)
            return oid.equals(that.oid);

        return false;
    }

    @Override
    public int hashCode() {
        return oid != null ? oid.hashCode() : super.hashCode();
    }
}
