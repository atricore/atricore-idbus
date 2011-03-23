package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityMappingPolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListIdentityMappingPolicyResponse {
    private List<IdentityMappingPolicy> accountLinkagePolicies;

    public List<IdentityMappingPolicy> getAccountLinkagePolicies() {
        if(accountLinkagePolicies == null){
            accountLinkagePolicies = new ArrayList<IdentityMappingPolicy>();
        }
        return accountLinkagePolicies;
    }

    public void setAccountLinkagePolicies(List<IdentityMappingPolicy> accountLinkagePolicies) {
        this.accountLinkagePolicies = accountLinkagePolicies;
    }
    
}
