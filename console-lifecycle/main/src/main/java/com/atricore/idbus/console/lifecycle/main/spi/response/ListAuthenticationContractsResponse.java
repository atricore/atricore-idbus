package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AuthenticationContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAuthenticationContractsResponse {

    private List<AuthenticationContract> authContracts;

    public List<AuthenticationContract> getAuthContracts() {
        if(authContracts == null){
            authContracts = new ArrayList<AuthenticationContract>();
        }
        return authContracts;
    }

    public void setAuthContracts(List<AuthenticationContract> authContracts) {
        this.authContracts = authContracts;
    }
}
