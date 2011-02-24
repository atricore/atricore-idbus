package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

public class JDOUserAttributeValue implements Serializable {

    private static final long serialVersionUID = 7061464839521161182L;
    
    private long id;

    private String name;

    private String value;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOUserAttributeValue)) return false;

        JDOUserAttributeValue that = (JDOUserAttributeValue) o;

        if(id == 0) return false;
        if (id != that.id) return false;

        return true;
    }
}
