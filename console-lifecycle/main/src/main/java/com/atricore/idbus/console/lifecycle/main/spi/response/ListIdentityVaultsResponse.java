package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentitySource;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListIdentityVaultsResponse {

    private List<IdentitySource> identitySources;

    public List<IdentitySource> getIdentityVaults() {
        if(identitySources == null){
            identitySources = new ArrayList<IdentitySource>();
        }
        return identitySources;
    }

    public void setIdentityVaults(List<IdentitySource> identitySources) {
        this.identitySources = identitySources;
    }
}
