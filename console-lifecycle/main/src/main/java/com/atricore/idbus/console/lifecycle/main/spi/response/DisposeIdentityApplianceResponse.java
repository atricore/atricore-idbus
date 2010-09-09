package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * @version $Id$
 */
public class DisposeIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityAppliance appliance;

    public DisposeIdentityApplianceResponse() {
    }

    public DisposeIdentityApplianceResponse(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

}
