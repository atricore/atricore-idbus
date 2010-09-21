package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.io.Serializable;
import java.util.Set;

/**
 * TODO : Subclass to support PHP, Liferay, Alfresco, Apache, ISAPI, PHPBB, 
 *
 * Author: Dejan Maric
 */
public class ExecutionEnvironment implements Serializable {

    private long id;
    private String name;
    private String displayName;
    private String description;
    private String installUri;
    private String platformId;
    private boolean active;
    private boolean overwriteOriginalSetup;
    private boolean installDemoApps;

    private Set<Activation> activations;

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

    public String getInstallUri() {
        return installUri;
    }

    public void setInstallUri(String installUri) {
        this.installUri = installUri;
    }

    public Set<Activation> getActivations() {
        return activations;
    }

    public void setActivations(Set<Activation> activations) {
        this.activations = activations;
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

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOverwriteOriginalSetup() {
        return overwriteOriginalSetup;
    }

    public void setOverwriteOriginalSetup(boolean overwriteOriginalSetup) {
        this.overwriteOriginalSetup = overwriteOriginalSetup;
    }

    public boolean isInstallDemoApps() {
        return installDemoApps;
    }

    public void setInstallDemoApps(boolean installDemoApps) {
        this.installDemoApps = installDemoApps;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExecutionEnvironment)) return false;

        ExecutionEnvironment that = (ExecutionEnvironment) o;

        if(id == 0) return false;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

}
