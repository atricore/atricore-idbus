package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationMechanism;

/**
 * Author: Dejan Maric
 */
public class LookupAuthenticationMechanismByIdResponse {

    private AuthenticationMechanism authenticationMechanism;

    public AuthenticationMechanism getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    public void setAuthenticationMechanism(AuthenticationMechanism authenticationMechanism) {
        this.authenticationMechanism = authenticationMechanism;
    }
}
