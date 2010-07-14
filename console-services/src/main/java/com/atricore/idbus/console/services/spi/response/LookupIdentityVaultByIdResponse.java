package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityVaultDTO;

/**
 * Author: Dejan Maric
 */
public class LookupIdentityVaultByIdResponse {

    private IdentityVaultDTO identityVault;

    public IdentityVaultDTO getIdentityVault() {
        return identityVault;
    }

    public void setIdentityVault(IdentityVaultDTO identityVault) {
        this.identityVault = identityVault;
    }
}
