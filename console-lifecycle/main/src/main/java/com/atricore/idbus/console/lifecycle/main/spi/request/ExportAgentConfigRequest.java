package com.atricore.idbus.console.lifecycle.main.spi.request;

public class ExportAgentConfigRequest extends AbstractManagementRequest {

    private String applianceId;

    private String execEnvName;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public String getExecEnvName() {
        return execEnvName;
    }

    public void setExecEnvName(String execEnvName) {
        this.execEnvName = execEnvName;
    }
}
