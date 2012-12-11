package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class ActivateResourceRequest extends AbstractManagementRequest {

    private String applianceId;

    private String resourceName;

    private boolean reactivate;

    private boolean replace;

    private String username;

    private String password;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String spName) {
        this.resourceName = spName;
    }

    public boolean isReactivate() {
        return reactivate;
    }

    public void setReactivate(boolean reactivate) {
        this.reactivate = reactivate;
    }

    public boolean isReplace() {
        return replace;
    }

    public void setReplace(boolean replace) {
        this.replace = replace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
