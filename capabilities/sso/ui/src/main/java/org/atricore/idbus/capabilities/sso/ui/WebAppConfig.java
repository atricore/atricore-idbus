package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.IClusterable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebAppConfig implements IClusterable {

    private String appName;

    private WebAppBranding branding;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public WebAppBranding getBranding() {
        return branding;
    }

    public void setBranding(WebAppBranding branding) {
        this.branding = branding;
    }
}
