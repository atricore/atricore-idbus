package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.ImpersonateUserPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListImpersonateUserPoliciesResponse {
    
    private List<ImpersonateUserPolicy> impersonateUserPolicies;

    public List<ImpersonateUserPolicy> getImpersonateUserPolicies() {
        if(impersonateUserPolicies == null){
            impersonateUserPolicies = new ArrayList<ImpersonateUserPolicy>();
        }
        return impersonateUserPolicies;
    }

    public void setImpersonateUserPolicies(List<ImpersonateUserPolicy> impersonateUserPolicies) {
        this.impersonateUserPolicies = impersonateUserPolicies;
    }    
}
