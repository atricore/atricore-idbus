package com.atricore.idbus.console.services.dto.branding;

import java.io.Serializable;

public class BrandingDefinitionDTO implements Serializable {

    private static final long serialVersionUID = -9080930098790122856L;

    private long id;
    
    private String name;

    private String description;
    
    //private String[] locales;
    
    private String defaultLocale;
    
    private String webBrandingId;

    protected BrandingTypeDTO type;

    // Path to resources

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWebBrandingId() {
        return webBrandingId;
    }

    public void setWebBrandingId(String webBrandingId) {
        this.webBrandingId = webBrandingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public String[] getLocales() {
        return locales;
    }

    public void setLocales(String[] locales) {
        this.locales = locales;
    }*/

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public BrandingTypeDTO getType() {
        return type;
    }

    public void setType(BrandingTypeDTO type) {
        this.type = type;
    }
}
