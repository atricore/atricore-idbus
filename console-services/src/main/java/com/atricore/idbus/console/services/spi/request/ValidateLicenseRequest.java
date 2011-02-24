package com.atricore.idbus.console.services.spi.request;

import com.atricore.idbus.console.services.dto.ResourceDTO;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ValidateLicenseRequest {

    private ResourceDTO license;

    public ResourceDTO getLicense() {
        return license;
    }

    public void setLicense(ResourceDTO license) {
        this.license = license;
    }

}
