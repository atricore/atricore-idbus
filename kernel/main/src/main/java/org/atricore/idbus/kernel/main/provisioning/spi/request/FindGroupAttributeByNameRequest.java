package org.atricore.idbus.kernel.main.provisioning.spi.request;

public class FindGroupAttributeByNameRequest extends AbstractProvisioningRequest {

    private static final long serialVersionUID = -2139068098156498718L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
