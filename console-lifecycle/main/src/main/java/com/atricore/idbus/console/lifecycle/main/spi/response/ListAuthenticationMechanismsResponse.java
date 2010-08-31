package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationMechanism;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAuthenticationMechanismsResponse {

    private List<AuthenticationMechanism> authMechanisms;

    public List<AuthenticationMechanism> getAuthMechanisms() {
        if(authMechanisms == null){
            authMechanisms = new ArrayList<AuthenticationMechanism>();
        }
        return authMechanisms;
    }

    public void setAuthMechanisms(List<AuthenticationMechanism> authMechanisms) {
        this.authMechanisms = authMechanisms;
    }
}
