package org.atricore.idbus.kernel.main.provisioning.impl;

import org.atricore.idbus.kernel.main.provisioning.spi.IdentityPartition;
import org.atricore.idbus.kernel.main.provisioning.spi.ProvisioningTarget;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ProvisioningTargetImpl implements ProvisioningTarget {

    private String name;

    private String description;
    
    private IdentityPartition identityPartition;

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

    public IdentityPartition getIdentityPartition() {
        return identityPartition;
    }

    public void setIdentityPartition(IdentityPartition identityPartition) {
        this.identityPartition = identityPartition;
    }

}
