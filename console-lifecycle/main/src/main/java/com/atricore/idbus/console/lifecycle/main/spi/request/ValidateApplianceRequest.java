package com.atricore.idbus.console.lifecycle.main.spi.request;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ValidateApplianceRequest extends AbstractManagementRequest {

    private String applianceId;

    private IdentityAppliance appliance;

    public String getApplianceId() {
        return applianceId;
    }

    public void setApplianceId(String applianceId) {
        this.applianceId = applianceId;
    }

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }
}
