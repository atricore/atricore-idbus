package org.atricore.idbus.kernel.main.provisioning.spi;

import org.atricore.idbus.kernel.main.store.identity.IdentityStore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    private IdentityPartition identityPartition;

    private MediationPartition mediationPartition;

    private Map<String, IdentityResource> identityResources = new HashMap<String, IdentityResource>();

    private ProvisioningTarget target;

    private ProvisioningEngine engine;

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
        return identityPartition.getIdentityStore();
    }

    public IdentityVault getVault() {
        return vault;
    }

    public void setVault(IdentityVault vault) {
        this.vault = vault;
    }

    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public void setIdentityPartition(IdentityPartition identityPartition) {
        this.identityPartition = identityPartition;
    }

    public MediationPartition getMediationPartition() {
        return mediationPartition;
    }

    public void setMediationPartition(MediationPartition mediationPartition) {
        this.mediationPartition = mediationPartition;
    }

    public ProvisioningTarget getTarget() {
        return target;
    }

    public void setTarget(ProvisioningTarget target) {
        this.target = target;
    }

    public ProvisioningEngine getEngine() {
        return engine;
    }

    public void setEngine(ProvisioningEngine engine) {
        this.engine = engine;
    }

    public IdentityResource lookupResource(String oid) {
        return identityResources.get(oid);
    }

    public void registerResource(IdentityResource resource) {
        identityResources.put(resource.getOid(), resource);
    }

    public Collection<IdentityResource> getResources() {
        return identityResources.values();
    }

    @Override
    public String toString() {
        return name + ">vault:" + (vault != null ? vault.getName() : "null") +
                ", partition:" + (identityPartition != null ? identityPartition.getName() : "null") +
                ", target:" + (target != null ? target.getName() : "null") +
                ", store:" + (getStore() != null ? getStore() : "null");
    }
}
