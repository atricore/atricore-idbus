package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.AccountLinkagePolicy;

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
