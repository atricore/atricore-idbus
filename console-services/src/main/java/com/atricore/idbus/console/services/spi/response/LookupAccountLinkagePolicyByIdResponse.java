package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AccountLinkagePolicyDTO;

/**
 * Author: Dejan Maric
 */
public class LookupAccountLinkagePolicyByIdResponse {

    private AccountLinkagePolicyDTO accountLinkagePolicy;

    public AccountLinkagePolicyDTO getAccountLinkagePolicy() {
        return accountLinkagePolicy;
    }

    public void setAccountLinkagePolicy(AccountLinkagePolicyDTO accountLinkagePolicy) {
        this.accountLinkagePolicy = accountLinkagePolicy;
    }
}
