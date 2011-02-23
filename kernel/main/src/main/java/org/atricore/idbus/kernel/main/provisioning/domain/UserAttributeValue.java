package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserAttributeValue implements Serializable {

    private static final long serialVersionUID = 4595183658527197264L;

    private long id;

    private String name;

    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAttributeValue)) return false;

        UserAttributeValue that = (UserAttributeValue) o;

        if(id == 0) return false;
        if (id != that.id) return false;

        return true;
    }
}
