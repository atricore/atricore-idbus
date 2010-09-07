package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;


/**
 * Author: Dejan Maric
 */
public class CreateSimpleSsoResponse extends AbstractManagementResponse {

    private IdentityApplianceDTO appliance;

    public IdentityApplianceDTO getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityApplianceDTO appliance) {
        this.appliance = appliance;
    }
}