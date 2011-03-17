package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivateExecEnvRequest extends AbstractManagementRequest {

    private String applianceId;

    private String execEnvName;

    private boolean reactivate;

    private boolean replace;

    private boolean activateSamples;

    private String username;

    private String password;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public String getExecEnvName() {
        return execEnvName;
    }

    public void setExecEnvName(String spName) {
        this.execEnvName = spName;
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

    public boolean isActivateSamples() {
        return activateSamples;
    }

    public void setActivateSamples(boolean activateSamples) {
        this.activateSamples = activateSamples;
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
