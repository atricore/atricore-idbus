package org.atricore.idbus.kernel.main.provisioning.spi.request;

public class RemoveUserAttributeRequest extends AbstractProvisioningRequest {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
