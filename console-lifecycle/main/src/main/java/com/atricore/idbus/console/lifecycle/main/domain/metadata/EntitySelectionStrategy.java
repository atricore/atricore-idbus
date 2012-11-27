package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;

/**
 */
public class EntitySelectionStrategy implements Serializable {

    private static final long serialVersionUID = -2865721004861436370L;

    private String name;

    private String description;

    public EntitySelectionStrategy() {
    }

    public EntitySelectionStrategy(String name, String description) {
        this.name = name;
        this.description = description;
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
}
