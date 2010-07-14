package com.atricore.idbus.console.services.spi.request;

/**
 * Author: Dejan Maric
 */
public class LookupAuthenticationContractByIdRequest {

    private long authenticationContactId;

    public long getAuthenticationContactId() {
        return authenticationContactId;
    }

    public void setAuthenticationContactId(long authenticationContactId) {
        this.authenticationContactId = authenticationContactId;
    }
}
