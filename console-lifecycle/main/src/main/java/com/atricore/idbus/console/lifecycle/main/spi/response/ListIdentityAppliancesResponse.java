package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Author: Dejan Maric
 */
public class ListIdentityAppliancesResponse extends AbstractManagementResponse {

    private Collection<IdentityAppliance> identityAppliances;

    public Collection<IdentityAppliance> getIdentityAppliances() {
        if(identityAppliances == null){
            identityAppliances = new ArrayList<IdentityAppliance>();
        }
        return identityAppliances;
    }

    public void setIdentityAppliances(Collection<IdentityAppliance> identityAppliances) {
        this.identityAppliances = identityAppliances;
    }
}
