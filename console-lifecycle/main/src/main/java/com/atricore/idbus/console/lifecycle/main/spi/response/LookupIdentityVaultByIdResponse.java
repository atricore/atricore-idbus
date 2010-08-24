package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentitySource;

/**
 * Author: Dejan Maric
 */
public class LookupIdentityVaultByIdResponse {

    private IdentitySource identitySource;

    public IdentitySource getIdentityVault() {
        return identitySource;
    }

    public void setIdentityVault(IdentitySource identitySource) {
        this.identitySource = identitySource;
    }
}
