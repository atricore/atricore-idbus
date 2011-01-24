package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * Author: Dejan Maric
 */

public class RemoveIdentityApplianceRequest extends AbstractManagementRequest {

    private String applianceId;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }
}
