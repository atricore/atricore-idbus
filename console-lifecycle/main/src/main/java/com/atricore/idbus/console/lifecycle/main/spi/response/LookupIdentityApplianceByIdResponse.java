package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * Author: Dejan Maric
 */
public class LookupIdentityApplianceByIdResponse extends AbstractManagementResponse {

    private IdentityAppliance identityAppliance;

    public IdentityAppliance getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityAppliance identityAppliance) {
        this.identityAppliance = identityAppliance;
    }

}
