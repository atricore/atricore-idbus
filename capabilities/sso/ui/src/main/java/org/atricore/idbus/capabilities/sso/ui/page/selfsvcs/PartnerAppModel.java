package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 3/12/13
 */
public class PartnerAppModel implements Serializable {
    private String id;

    private String name;

    private String displayName;

    private String description;

    private String ssoEndpoint;

    private String resourceType;

    // TODO : Icon !

    // TODO : IdP Initiated SSO Link


    public PartnerAppModel(String id,
                           String name,
                           String displayName,
                           String description,
                           String ssoEndpoint,
                           String resourceType) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.ssoEndpoint = ssoEndpoint;
        this.resourceType = resourceType;
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

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
