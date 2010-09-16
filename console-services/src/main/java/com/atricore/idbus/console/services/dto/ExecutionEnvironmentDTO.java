package com.atricore.idbus.console.services.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Dejan Maric
 */
public class ExecutionEnvironmentDTO implements Serializable {

    private long id;
    private String name;
    private String displayName;
    private String description;
    private String installUri;
    private String platformId;
    private boolean active;

    private Set<ActivationDTO> activations;

    private static final long serialVersionUID = 175340870033867780L;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstallUri() {
        return installUri;
    }

    public void setInstallUri(String installUri) {
        this.installUri = installUri;
    }

    public Set<ActivationDTO> getActivations() {
        if(activations == null){
            activations = new HashSet<ActivationDTO>();
        }
        return activations;
    }

    public void setActivations(Set<ActivationDTO> activations) {
        this.activations = activations;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionEnvironmentDTO)) return false;

        ExecutionEnvironmentDTO that = (ExecutionEnvironmentDTO) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}