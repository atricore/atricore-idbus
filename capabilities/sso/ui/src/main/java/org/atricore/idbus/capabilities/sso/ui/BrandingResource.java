package org.atricore.idbus.capabilities.sso.ui;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingResource {

    private String id;

    private String path;

    private String value;

    private BrandingResourceType type;

    public BrandingResource() {

    }

    public BrandingResource(String id, String path, String value, BrandingResourceType type) {
        this.id = id;
        this.path = path;
        this.value = value;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BrandingResourceType getType() {
        return type;
    }

    public void setType(BrandingResourceType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
