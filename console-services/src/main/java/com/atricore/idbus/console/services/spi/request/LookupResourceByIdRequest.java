package com.atricore.idbus.console.services.spi.request;

import org.atricore.idbus.capabilities.management.main.spi.request.AbstractManagementRequest;

public class LookupResourceByIdRequest extends AbstractManagementRequest {

    private String resourceId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}