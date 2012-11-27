package com.atricore.idbus.console.services.dto;

import java.io.Serializable;

/**
 *
 */
public class EntitySelectionStrategyDTO implements Serializable {

    private static final long serialVersionUID = -2637953445913184888L;

    private String name;

    private String description;

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
