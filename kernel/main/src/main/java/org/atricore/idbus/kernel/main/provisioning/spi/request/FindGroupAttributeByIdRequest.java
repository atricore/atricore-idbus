package org.atricore.idbus.kernel.main.provisioning.spi.request;

public class FindGroupAttributeByIdRequest extends AbstractProvisioningRequest {

    private static final long serialVersionUID = -2039068098156498718L;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
