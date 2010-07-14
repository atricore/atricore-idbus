package com.atricore.idbus.console.services.spi.response;


import com.atricore.idbus.console.services.dto.AuthenticationContractDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAuthenticationContractsResponse {

    private List<AuthenticationContractDTO> authContracts;

    public List<AuthenticationContractDTO> getAuthContracts() {
        if(authContracts == null){
            authContracts = new ArrayList<AuthenticationContractDTO>();
        }
        return authContracts;
    }

    public void setAuthContracts(List<AuthenticationContractDTO> authContracts) {
        this.authContracts = authContracts;
    }
}
