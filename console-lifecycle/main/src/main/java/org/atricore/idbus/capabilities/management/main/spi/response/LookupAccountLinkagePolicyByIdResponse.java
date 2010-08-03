package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.AccountLinkagePolicy;

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
