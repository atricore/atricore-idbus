package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AccountLinkagePolicy;

/**
 * Author: Dejan Maric
 */
public class LookupAccountLinkagePolicyByIdResponse {

    private AccountLinkagePolicy accountLinkagePolicy;

    public AccountLinkagePolicy getAccountLinkagePolicy() {
        return accountLinkagePolicy;
    }

    public void setAccountLinkagePolicy(AccountLinkagePolicy accountLinkagePolicy) {
        this.accountLinkagePolicy = accountLinkagePolicy;
    }
}
