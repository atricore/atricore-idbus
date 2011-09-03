package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityMappingPolicyDTO;

import java.util.ArrayList;
import java.util.List;

public class ListIdentityMappingPoliciesResponse {

    private List<IdentityMappingPolicyDTO> identityMappingPolicies;

    public List<IdentityMappingPolicyDTO> getIdentityMappingPolicies() {
        if(identityMappingPolicies == null){
            identityMappingPolicies = new ArrayList<IdentityMappingPolicyDTO>();
        }
        return identityMappingPolicies;
    }

    public void setIdentityMappingPolicies(List<IdentityMappingPolicyDTO> identityMappingPolicies) {
        this.identityMappingPolicies = identityMappingPolicies;
    }
}
