package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityVault;

/**
 * Author: Dejan Maric
 */
public class LookupIdentityVaultByIdResponse {

    private IdentityVault identityVault;

    public IdentityVault getIdentityVault() {
        return identityVault;
    }

    public void setIdentityVault(IdentityVault identityVault) {
        this.identityVault = identityVault;
    }
}
