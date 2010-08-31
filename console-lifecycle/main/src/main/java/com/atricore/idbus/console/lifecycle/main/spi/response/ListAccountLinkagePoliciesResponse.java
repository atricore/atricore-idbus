package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AccountLinkagePolicy;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAccountLinkagePoliciesResponse {

    private List<AccountLinkagePolicy> accountLinkagePolicies;

    public List<AccountLinkagePolicy> getAccountLinkagePolicies() {
        if(accountLinkagePolicies == null){
            accountLinkagePolicies = new ArrayList<AccountLinkagePolicy>();
        }
        return accountLinkagePolicies;
    }

    public void setAccountLinkagePolicies(List<AccountLinkagePolicy> accountLinkagePolicies) {
        this.accountLinkagePolicies = accountLinkagePolicies;
    }
}
