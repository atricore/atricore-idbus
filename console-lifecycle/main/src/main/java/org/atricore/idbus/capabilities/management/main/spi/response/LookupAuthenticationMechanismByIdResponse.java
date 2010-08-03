package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.AuthenticationMechanism;

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
