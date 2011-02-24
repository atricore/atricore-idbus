package org.atricore.idbus.connectors.jdoidentityvault.domain;

import java.io.Serializable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JDOGroup implements Serializable {

    private static final long serialVersionUID = 4595183658527599864L;

    private Long id;
    private String name;
    private String description;

    //<--- Extended Attributes ---->
    private JDOGroupAttributeValue[] attrs;
    
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


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JDOGroupAttributeValue[] getAttrs() {
        return attrs;
    }

    public void setAttrs(JDOGroupAttributeValue[] attrs) {
        this.attrs = attrs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JDOGroup)) return false;

        JDOGroup that = (JDOGroup) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}