package com.atricore.idbus.console.brandservice.main;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ResourceBasedBrandingDefinition extends  BrandingDefinition {

    private String skin;

    private List<BrandingResource> resources = new ArrayList<BrandingResource>();

    public List<BrandingResource> getResources() {
        return resources;
    }

    public void setResources(List<BrandingResource> resources) {
        this.resources = resources;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }
}
