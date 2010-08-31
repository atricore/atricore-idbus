package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityVault;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractIdentityPartition implements IdentityPartition {

    private String name;

    private String description;

    private IdentityVault identityVault;

    private IdentityStore identityStore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IdentityVault getIdentityVault() {
        return identityVault;
    }

    public void setIdentityVault(IdentityVault identityVault) {
        this.identityVault = identityVault;
    }

    public IdentityStore getIdentityStore() {
        return identityStore;
    }

    public void setIdentityStore(IdentityStore identityStore) {
        this.identityStore = identityStore;
    }
}
