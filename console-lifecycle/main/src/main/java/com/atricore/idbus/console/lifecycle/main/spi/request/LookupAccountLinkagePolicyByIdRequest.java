package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * Author: Dejan Maric
 */
public class LookupAccountLinkagePolicyByIdRequest {

    private long accountLinkagePolicyId;

    public long getAccountLinkagePolicyId() {
        return accountLinkagePolicyId;
    }

    public void setAccountLinkagePolicyId(long accountLinkagePolicyId) {
        this.accountLinkagePolicyId = accountLinkagePolicyId;
    }
}
