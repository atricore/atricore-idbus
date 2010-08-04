package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

import java.util.Collection;

/**
 * Author: Dejan Maric
 */
public class ListIdentityAppliancesByStateResponse extends AbstractManagementResponse {

    private Collection<IdentityAppliance> appliances;

    public Collection<IdentityAppliance> getAppliances() {
        return appliances;
    }

    public void setAppliances(Collection<IdentityAppliance> appliances) {
        this.appliances = appliances;
    }
}
