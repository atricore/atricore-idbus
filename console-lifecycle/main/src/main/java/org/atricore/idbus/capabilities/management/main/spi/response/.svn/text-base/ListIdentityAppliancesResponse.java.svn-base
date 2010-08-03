package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;

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
