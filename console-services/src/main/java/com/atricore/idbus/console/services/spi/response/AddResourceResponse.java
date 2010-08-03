package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.ResourceDTO;
import com.atricore.idbus.console.lifecycle.main.spi.response.AbstractManagementResponse;

public class AddResourceResponse extends AbstractManagementResponse {

    private ResourceDTO resource;

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }
}