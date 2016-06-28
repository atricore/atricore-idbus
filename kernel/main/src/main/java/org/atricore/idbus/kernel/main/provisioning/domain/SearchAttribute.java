package org.atricore.idbus.kernel.main.provisioning.domain;

import java.io.Serializable;

public class SearchAttribute implements Serializable {

    private static final long serialVersionUID = -2706586657476531796L;

    private String name;

    private String value;

    private AttributeType type;

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

    public AttributeType getType() {
        return type;
    }

    public void setType(AttributeType type) {
        this.type = type;
    }
}
