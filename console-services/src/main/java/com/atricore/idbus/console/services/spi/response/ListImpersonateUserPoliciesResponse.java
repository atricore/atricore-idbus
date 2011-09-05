package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.ImpersonateUserPolicyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListImpersonateUserPoliciesResponse extends AbstractManagementResponse {
    
    private List<ImpersonateUserPolicyDTO> impersonateUserPolicies;

    public List<ImpersonateUserPolicyDTO> getImpersonateUserPolicies() {
        if(impersonateUserPolicies == null){
            impersonateUserPolicies = new ArrayList<ImpersonateUserPolicyDTO>();
        }
        return impersonateUserPolicies;
    }

    public void setImpersonateUserPolicies(List<ImpersonateUserPolicyDTO> identityMappingPolicies) {
        this.impersonateUserPolicies = identityMappingPolicies;
    }


    
}
