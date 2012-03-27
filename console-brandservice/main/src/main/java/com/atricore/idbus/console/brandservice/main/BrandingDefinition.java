package com.atricore.idbus.console.brandservice.main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BrandingDefinition implements Serializable {

    private long id;
    
    private String name;

    private String description;
    
    private String[] locales;
    
    private String defaultLocale;
    
    private String webBrandingId;

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

    public String[] getLocales() {
        return locales;
    }

    public void setLocales(String[] locales) {
        this.locales = locales;
    }

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
}
