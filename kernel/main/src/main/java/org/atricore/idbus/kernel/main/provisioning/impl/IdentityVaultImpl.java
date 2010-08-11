package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityVault;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityVaultImpl implements IdentityVault {

    private String name;

    private String description;

    private Set<IdentityPartition> identityPartitions;

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

    public Set<IdentityPartition> getIdentityPartitions() {
        return identityPartitions;
    }

    public void setIdentityPartitions(Set<IdentityPartition> identityPartitions) {
        this.identityPartitions = identityPartitions;
    }
}
