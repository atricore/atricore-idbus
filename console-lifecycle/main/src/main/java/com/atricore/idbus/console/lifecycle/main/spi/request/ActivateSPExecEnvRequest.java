package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivateSPExecEnvRequest extends AbstractManagementRequest {

    private String applianceId;

    private String execEnvName;

    private boolean reactivate;

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
}
