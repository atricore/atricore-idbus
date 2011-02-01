package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.LicenseTypeDTO;

/**
 * Author: Dejan Maric
 */
public class GetLicenseResponse {

    private LicenseTypeDTO license;
    private String error;

    public LicenseTypeDTO getLicense() {
        return license;
    }

    public void setLicense(LicenseTypeDTO license) {
        this.license = license;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
