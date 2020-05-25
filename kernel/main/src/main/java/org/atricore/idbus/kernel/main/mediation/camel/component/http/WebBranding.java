package org.atricore.idbus.kernel.main.mediation.camel.component.http;

import java.io.Serializable;

public class WebBranding implements Serializable {

    private String name;

    private String defaultLocale;

    private String webBrandingId;

    public WebBranding() {

    }

    public WebBranding(String name, String defaultLocale, String webBrandingId) {
        this.name = name;
        this.defaultLocale = defaultLocale;
        this.webBrandingId = webBrandingId;
    }

    public String getName() {
        return name;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public String getWebBrandingId() {
        return webBrandingId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public void setWebBrandingId(String webBrandingId) {
        this.webBrandingId = webBrandingId;
    }
}
