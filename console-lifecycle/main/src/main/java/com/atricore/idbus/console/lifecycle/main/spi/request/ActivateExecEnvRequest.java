package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivateExecEnvRequest extends AbstractManagementRequest {

    private String applianceId;

    private String execEnvName;

    private boolean reactivate;

    private boolean replace;

    private boolean activateSamples;

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
}
