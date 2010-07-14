package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AuthenticationContractDTO;

/**
 * Author: Dejan Maric
 */
public class LookupAuthenticationContractByIdResponse {

    private AuthenticationContractDTO authenticationContract;

    public AuthenticationContractDTO getAuthenticationContract() {
        return authenticationContract;
    }

    public void setAuthenticationContract(AuthenticationContractDTO authenticationContract) {
        this.authenticationContract = authenticationContract;
    }
}
