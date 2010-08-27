package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentitySourceDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListIdentityVaultsResponse {

    private List<IdentitySourceDTO> identityVaults;

    public List<IdentitySourceDTO> getIdentityVaults() {
        if(identityVaults == null){
            identityVaults = new ArrayList<IdentitySourceDTO>();
        }
        return identityVaults;
    }

    public void setIdentityVaults(List<IdentitySourceDTO> identityVaults) {
        this.identityVaults = identityVaults;
    }
}
