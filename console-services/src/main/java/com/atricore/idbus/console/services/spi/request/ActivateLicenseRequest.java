package com.atricore.idbus.console.services.spi.request;

import com.atricore.idbus.console.services.dto.ResourceDTO;

/**
 * Author: Dejan Maric
 */
public class ActivateLicenseRequest {

    private ResourceDTO license;

    public ResourceDTO getLicense() {
        return license;
    }

    public void setLicense(ResourceDTO license) {
        this.license = license;
    }
}
