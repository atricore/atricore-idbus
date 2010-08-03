package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;

/**
 * Author: Dejan Maric
 */
public class AddIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityAppliance appliance;

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }
}



