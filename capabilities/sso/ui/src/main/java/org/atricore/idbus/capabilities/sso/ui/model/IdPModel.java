package org.atricore.idbus.capabilities.sso.ui.model;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 6/17/13
 */
public class IdPModel implements Serializable {

    private String id;

    private String name;

    private String displayName;

    private String description;

    private String entityId;

    private String ssoEndpoint;

    private String sloEndpoint;

    private String providerType;

    private Boolean rememberSelection;

    public IdPModel(String id,
                           String name,
                           String displayName,
                           String description,
                           String entityId,
                           String ssoEndpoint,
                           String sloEndpoint,
                           String providerType) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.entityId = entityId;
        this.ssoEndpoint = ssoEndpoint;
        this.sloEndpoint = sloEndpoint;
        this.providerType = providerType;
        this.rememberSelection = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getSsoEndpoint() {
        return ssoEndpoint;
    }

    public void setSsoEndpoint(String ssoEndpoint) {
        this.ssoEndpoint = ssoEndpoint;
    }

    public String getSloEndpoint() {
        return sloEndpoint;
    }

    public void setSloEndpoint(String sloEndpoint) {
        this.sloEndpoint = sloEndpoint;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Boolean getRememberSelection() {
        return rememberSelection;
    }

    public void setRememberSelection(Boolean rememberSelection) {
        this.rememberSelection = rememberSelection;
    }
}
