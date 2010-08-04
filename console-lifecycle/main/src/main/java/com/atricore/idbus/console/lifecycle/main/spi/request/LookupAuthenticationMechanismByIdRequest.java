package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * Author: Dejan Maric
 */
public class LookupAuthenticationMechanismByIdRequest {

    private long authMechanismId;

    public long getAuthMechanismId() {
        return authMechanismId;
    }

    public void setAuthMechanismId(long authMechanismId) {
        this.authMechanismId = authMechanismId;
    }
}
