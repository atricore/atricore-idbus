package com.atricore.idbus.console.lifecycle.main.domain;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;

import java.io.Serializable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdentityAppliance implements Serializable {

    private static final long serialVersionUID = 871536646583177663L;

    private long id;

    private String state;

    private String name;

    private String displayName;

    private String description;


    private IdentityApplianceDefinition idApplianceDefinition;

    private IdentityApplianceDeployment idApplianceDeployment;
    
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public IdentityApplianceDefinition getIdApplianceDefinition() {
        return idApplianceDefinition;
    }

    public void setIdApplianceDefinition(IdentityApplianceDefinition idApplianceDefinition) {
        this.idApplianceDefinition = idApplianceDefinition;
    }

    public IdentityApplianceDeployment getIdApplianceDeployment() {
        return idApplianceDeployment;
    }

    public void setIdApplianceDeployment(IdentityApplianceDeployment idAppliance) {
        this.idApplianceDeployment = idAppliance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdentityAppliance)) return false;

        IdentityAppliance appliance = (IdentityAppliance) o;

        if(id == 0) return false;

        if (id != appliance.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
