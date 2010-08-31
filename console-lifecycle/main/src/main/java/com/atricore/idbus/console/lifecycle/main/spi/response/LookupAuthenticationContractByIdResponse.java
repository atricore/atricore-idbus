package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationContract;

/**
 * Author: Dejan Maric
 */
public class LookupAuthenticationContractByIdResponse {

    private AuthenticationContract authenticationContract;

    public AuthenticationContract getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContract authenticationContract) {
        this.authenticationContract = authenticationContract;
    }
}
