package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListIdentityApplianceDefinitionsResponse extends AbstractManagementResponse {

    private List<IdentityApplianceDefinition> identityApplianceDefinitions;

    public List<IdentityApplianceDefinition> getIdentityApplianceDefinitions() {
        if(identityApplianceDefinitions == null){
            identityApplianceDefinitions = new ArrayList<IdentityApplianceDefinition>();
        }
        return identityApplianceDefinitions;
    }

    public void setIdentityApplianceDefinitions(List<IdentityApplianceDefinition> identityApplianceDefinitions) {
        this.identityApplianceDefinitions = identityApplianceDefinitions;
    }
}
