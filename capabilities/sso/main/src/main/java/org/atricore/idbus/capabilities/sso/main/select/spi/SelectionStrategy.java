package org.atricore.idbus.capabilities.sso.main.select.spi;

import org.atricore.idbus.capabilities.sso.main.select.spi.EntitySelector;

import java.util.List;

/**
 */
public class SelectionStrategy {

    private String name;

    private List<EntitySelector> selectors;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EntitySelector> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<EntitySelector> selectors) {
        this.selectors = selectors;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
