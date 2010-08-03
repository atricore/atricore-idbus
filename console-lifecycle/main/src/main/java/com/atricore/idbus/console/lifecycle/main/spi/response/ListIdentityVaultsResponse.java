package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityVault;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListIdentityVaultsResponse {

    private List<IdentityVault> identityVaults;

    public List<IdentityVault> getIdentityVaults() {
        if(identityVaults == null){
            identityVaults = new ArrayList<IdentityVault>();
        }
        return identityVaults;
    }

    public void setIdentityVaults(List<IdentityVault> identityVaults) {
        this.identityVaults = identityVaults;
    }
}
