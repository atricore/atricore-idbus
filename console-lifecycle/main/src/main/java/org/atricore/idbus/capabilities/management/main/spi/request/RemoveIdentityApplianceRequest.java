package org.atricore.idbus.capabilities.management.main.spi.request;

import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;

/**
 * Author: Dejan Maric
 */

public class RemoveIdentityApplianceRequest extends AbstractManagementRequest {

    private IdentityAppliance identityAppliance;

    public IdentityAppliance getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityAppliance identityAppliance) {
        this.identityAppliance = identityAppliance;
    }
}
