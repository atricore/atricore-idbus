package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityVault;

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
