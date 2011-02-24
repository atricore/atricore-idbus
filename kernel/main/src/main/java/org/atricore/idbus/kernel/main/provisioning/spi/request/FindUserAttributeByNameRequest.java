package org.atricore.idbus.kernel.main.provisioning.spi.request;

public class FindUserAttributeByNameRequest extends AbstractProvisioningRequest {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
