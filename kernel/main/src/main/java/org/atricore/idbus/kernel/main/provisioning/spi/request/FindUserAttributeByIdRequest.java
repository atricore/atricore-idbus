package org.atricore.idbus.kernel.main.provisioning.spi.request;

public class FindUserAttributeByIdRequest extends AbstractProvisioningRequest {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
