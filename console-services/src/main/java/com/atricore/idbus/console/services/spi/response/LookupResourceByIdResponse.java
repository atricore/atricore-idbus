package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.ResourceDTO;


public class LookupResourceByIdResponse extends AbstractManagementResponse {

    private ResourceDTO resource;

    public ResourceDTO getResource() {
        return resource;
    }

    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }
}