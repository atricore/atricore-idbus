package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/26/13
 */
public class UndisposeIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityAppliance appliance;

    public UndisposeIdentityApplianceResponse() {
    }

    public UndisposeIdentityApplianceResponse(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

}
