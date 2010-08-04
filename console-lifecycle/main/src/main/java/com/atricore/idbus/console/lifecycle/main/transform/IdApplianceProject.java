package com.atricore.idbus.console.lifecycle.main.transform;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdApplianceProject {

    private String id;

    private String description;

    private IdentityApplianceDefinition definition;

    private IdentityAppliance idAppliance;

    private IdProjectModule rootModule;

    public IdApplianceProject(String id) {
        this.id = id;
    }

    public IdApplianceProject(String id, String description) {
        this(id);
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public IdentityAppliance getIdAppliance() {
        return idAppliance;
    }

    public void setIdAppliance(IdentityAppliance idAppliance) {
        this.idAppliance = idAppliance;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDefinition(IdentityApplianceDefinition iaDefinition) {
        this.definition = iaDefinition;
    }

    public IdentityApplianceDefinition getDefinition() {
        return definition;
    }

    public void setRootModule(IdProjectModule rootModule) {
        this.rootModule = rootModule;
    }

    public IdProjectModule getRootModule() {
        return rootModule;
    }
}
