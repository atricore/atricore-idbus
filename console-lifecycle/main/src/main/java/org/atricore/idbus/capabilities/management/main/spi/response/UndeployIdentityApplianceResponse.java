package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;

/**
 * Author: Dejan Maric
 */
public class UndeployIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityAppliance appliance;

    public UndeployIdentityApplianceResponse() {

    }

    public UndeployIdentityApplianceResponse(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

    public IdentityAppliance getAppliance() {
        return appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }
}
