package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

public class JDOGroupAttributeValue implements Serializable {

    private static final long serialVersionUID = 1213326080821149664L;
    
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
        if (!(o instanceof JDOGroupAttributeValue)) return false;

        JDOGroupAttributeValue that = (JDOGroupAttributeValue) o;

        if(id == 0) return false;
        if (id != that.id) return false;

        return true;
    }
}
