package org.atricore.idbus.kernel.main.mediation.camel.component.http.ui;

import java.io.Serializable;
import java.util.Properties;

/**
 *  RFU : Simple branding definition.
 */
public class WebBranding implements Serializable {

    private String name;

    private String defaultLocale;

    private String webBrandingId;

    private Properties props;

    public WebBranding() {

    }

    public WebBranding(String name, String defaultLocale, String webBrandingId, Properties props) {
        this.name = name;
        this.defaultLocale = defaultLocale;
        this.webBrandingId = webBrandingId;
        this.props = props;
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

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }
}
