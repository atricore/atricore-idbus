package org.atricore.idbus.kernel.main.provisioning.spi.response;

import org.atricore.idbus.kernel.main.provisioning.domain.IdentityResourceDescriptor;

public class ListResourcesResponse extends AbstractProvisioningResponse {

    private IdentityResourceDescriptor[] resources;

    public IdentityResourceDescriptor[] getResources() {
        return resources;
    }

    public void setResources(IdentityResourceDescriptor[] resources) {
        this.resources = resources;
    }
}
