package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityMappingPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListIdentityMappingPoliciesResponse {
    private List<IdentityMappingPolicy> identityMappingPolicies;

    public List<IdentityMappingPolicy> getIdentityMappingPolicies() {
        if(identityMappingPolicies == null){
            identityMappingPolicies = new ArrayList<IdentityMappingPolicy>();
        }
        return identityMappingPolicies;
    }

    public void setIdentityMappingPolicies(List<IdentityMappingPolicy> identityMappingPolicies) {
        this.identityMappingPolicies = identityMappingPolicies;
    }
}
