package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AuthenticationMechanismDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAuthenticationMechanismsResponse {

    private List<AuthenticationMechanismDTO> authMechanisms;

    public List<AuthenticationMechanismDTO> getAuthMechanisms() {
        if(authMechanisms == null){
            authMechanisms = new ArrayList<AuthenticationMechanismDTO>();
        }
        return authMechanisms;
    }

    public void setAuthMechanisms(List<AuthenticationMechanismDTO> authMechanisms) {
        this.authMechanisms = authMechanisms;
    }
}
