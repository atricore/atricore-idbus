package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.ImpersonateUserPolicyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListImpersonateUserPoliciesResponse extends AbstractManagementResponse {
    
    private List<ImpersonateUserPolicyDTO> identityMappingPolicies;

    public List<ImpersonateUserPolicyDTO> getIdentityMappingPolicies() {
        if(identityMappingPolicies == null){
            identityMappingPolicies = new ArrayList<ImpersonateUserPolicyDTO>();
        }
        return identityMappingPolicies;
    }

    public void setIdentityMappingPolicies(List<ImpersonateUserPolicyDTO> identityMappingPolicies) {
        this.identityMappingPolicies = identityMappingPolicies;
    }
    
}
