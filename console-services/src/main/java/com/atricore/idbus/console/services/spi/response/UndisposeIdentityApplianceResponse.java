package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityApplianceDTO;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 7/26/13
 */
public class UndisposeIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityApplianceDTO appliance;

    public UndisposeIdentityApplianceResponse() {

    }

    public UndisposeIdentityApplianceResponse(IdentityApplianceDTO appliance) {
        this.appliance = appliance;
    }

    public IdentityApplianceDTO getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityApplianceDTO appliance) {
        this.appliance = appliance;
    }
}
