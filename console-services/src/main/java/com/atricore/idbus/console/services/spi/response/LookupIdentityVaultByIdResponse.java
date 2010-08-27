package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentitySourceDTO;

/**
 * Author: Dejan Maric
 */
public class LookupIdentityVaultByIdResponse {

    private IdentitySourceDTO identityVault;

    public IdentitySourceDTO getIdentityVault() {
        return identityVault;
    }

    public void setIdentityVault(IdentitySourceDTO identityVault) {
        this.identityVault = identityVault;
    }
}
