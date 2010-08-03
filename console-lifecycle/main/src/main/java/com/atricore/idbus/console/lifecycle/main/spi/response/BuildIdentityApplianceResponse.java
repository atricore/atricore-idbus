package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class BuildIdentityApplianceResponse extends AbstractManagementResponse {

    private IdentityAppliance appliance;

    public BuildIdentityApplianceResponse() {
        super();
    }

    public BuildIdentityApplianceResponse(IdentityAppliance appliance) {
        super();
        this.appliance = appliance;
    }

    public void setAppliance(IdentityAppliance appliance) {
        this.appliance = appliance;
    }

    public IdentityAppliance getAppliance() {
        return appliance;
    }
}
