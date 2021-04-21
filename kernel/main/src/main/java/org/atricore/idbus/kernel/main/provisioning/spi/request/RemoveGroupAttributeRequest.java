package org.atricore.idbus.kernel.main.provisioning.spi.request;

public class RemoveGroupAttributeRequest extends AbstractProvisioningRequest {

    private static final long serialVersionUID = -3939068098156498718L;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
