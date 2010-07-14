package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AuthenticationMechanismDTO;

/**
 * Author: Dejan Maric
 */
public class LookupAuthenticationMechanismByIdResponse {

    private AuthenticationMechanismDTO authenticationMechanism;

    public AuthenticationMechanismDTO getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanismDTO authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }
}
