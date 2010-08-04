package com.atricore.idbus.console.services.spi.request;

import com.atricore.idbus.console.lifecycle.main.spi.request.AbstractManagementRequest;

public class LookupResourceByIdRequest extends AbstractManagementRequest {

    private String resourceId;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}