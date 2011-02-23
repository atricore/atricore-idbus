package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOExtendedAttribute implements Serializable {

    private static final long serialVersionUID = 4595183697627599864L;

    private Long id;
    private String name;
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOExtendedAttribute)) return false;

        JDOExtendedAttribute that = (JDOExtendedAttribute) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }


    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}

