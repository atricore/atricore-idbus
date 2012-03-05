package org.atricore.idbus.capabilities.sso.ui;

import org.apache.wicket.IClusterable;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebAppConfig implements IClusterable {

    private String appName;

    private String brandingId;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBrandingId() {
        return brandingId;
    }

    public void setBrandingId(String brandingId) {
        this.brandingId = brandingId;
    }
}
