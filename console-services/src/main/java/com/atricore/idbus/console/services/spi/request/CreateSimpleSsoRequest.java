package com.atricore.idbus.console.services.spi.request;

import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

/**
 * Author: Dejan Maric
 */
public class CreateSimpleSsoRequest extends AbstractManagementRequest {

    private IdentityApplianceDTO identityAppliance;

    public IdentityApplianceDTO getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityApplianceDTO identityAppliance) {
        this.identityAppliance = identityAppliance;
    }
}