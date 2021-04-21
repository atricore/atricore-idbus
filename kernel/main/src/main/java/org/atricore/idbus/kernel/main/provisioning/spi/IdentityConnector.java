package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

/**
 * The identity connector provides information about a specific connection
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class IdentityConnector {

    private String name;

    private String description;

    // If true, the connector can be used from multiple appliances.

    private String storeName;

    private boolean shared;

    private IdentityVault vault;

    private IdentityPartition partition;

    private ProvisioningTarget target;

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

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public IdentityStore getStore() {
        return partition.getIdentityStore();
    }

    public IdentityVault getVault() {
        return vault;
    }

    public void setVault(IdentityVault vault) {
        this.vault = vault;
    }

    public IdentityPartition getPartition() {
        return partition;
    }

    public void setPartition(IdentityPartition partition) {
        this.partition = partition;
    }

    public ProvisioningTarget getTarget() {
        return target;
    }

    public void setTarget(ProvisioningTarget target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return name + ">vault:" + (vault != null ? vault.getName() : "null") +
                ", partition:" + (partition != null ? partition.getName() : "null") +
                ", target:" + (target != null ? target.getName() : "null") +
                ", store:" + (getStore() != null ? getStore() : "null");
    }
}
