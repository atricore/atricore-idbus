package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AccountLinkagePolicyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAccountLinkagePoliciesResponse {

    private List<AccountLinkagePolicyDTO> accountLinkagePolicies;

    public List<AccountLinkagePolicyDTO> getAccountLinkagePolicies() {
        if(accountLinkagePolicies == null){
            accountLinkagePolicies = new ArrayList<AccountLinkagePolicyDTO>();
        }
        return accountLinkagePolicies;
    }

    public void setAccountLinkagePolicies(List<AccountLinkagePolicyDTO> accountLinkagePolicies) {
        this.accountLinkagePolicies = accountLinkagePolicies;
    }
}
