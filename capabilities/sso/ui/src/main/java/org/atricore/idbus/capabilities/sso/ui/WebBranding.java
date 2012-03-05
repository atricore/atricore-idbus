package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.IClusterable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBranding implements IClusterable {

    private String description;

    private String id;

    private String skin;

    private Set<BrandingResource> resources = new HashSet<BrandingResource>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public Set<BrandingResource> getResources() {
        return resources;
    }

    public void setResources(Set<BrandingResource> resources) {
        this.resources = resources;
    }
}
