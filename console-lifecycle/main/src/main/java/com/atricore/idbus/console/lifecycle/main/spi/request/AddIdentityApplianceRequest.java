package com.atricore.idbus.console.lifecycle.main.spi.request;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * Author: Dejan Maric
 */
public class AddIdentityApplianceRequest extends AbstractManagementRequest {

    private IdentityAppliance identityAppliance;

    public IdentityAppliance getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityAppliance identityAppliance) {
        this.identityAppliance = identityAppliance;
    }
}
