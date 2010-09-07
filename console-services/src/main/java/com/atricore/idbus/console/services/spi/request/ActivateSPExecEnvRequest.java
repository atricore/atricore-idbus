package com.atricore.idbus.console.services.spi.request;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivateSPExecEnvRequest extends AbstractManagementRequest {

    private String applianceId;

    private String spName;

    private boolean reactivate;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public String getSPName() {
        return spName;
    }

    public void setSPName(String spName) {
        this.spName = spName;
    }

    public boolean isReactivate() {
        return reactivate;
    }

    public void setReactivate(boolean reactivate) {
        this.reactivate = reactivate;
    }

}
