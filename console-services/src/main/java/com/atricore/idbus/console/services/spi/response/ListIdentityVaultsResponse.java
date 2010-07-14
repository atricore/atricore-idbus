package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityVaultDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListIdentityVaultsResponse {

    private List<IdentityVaultDTO> identityVaults;

    public List<IdentityVaultDTO> getIdentityVaults() {
        if(identityVaults == null){
            identityVaults = new ArrayList<IdentityVaultDTO>();
        }
        return identityVaults;
    }

    public void setIdentityVaults(List<IdentityVaultDTO> identityVaults) {
        this.identityVaults = identityVaults;
    }
}
