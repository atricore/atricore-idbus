package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * Author: Dejan Maric
 */
public class LookupIdentityVaultByIdRequest {

    private long identityVaultId;

    public long getIdentityVaultId() {
        return identityVaultId;
    }

    public void setIdentityVaultId(long identityVaultId) {
        this.identityVaultId = identityVaultId;
    }
}
