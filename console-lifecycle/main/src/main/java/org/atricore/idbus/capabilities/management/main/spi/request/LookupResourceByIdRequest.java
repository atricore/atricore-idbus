package org.atricore.idbus.capabilities.management.main.spi.request;

public class LookupResourceByIdRequest extends AbstractManagementRequest {

    private String resourceId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}