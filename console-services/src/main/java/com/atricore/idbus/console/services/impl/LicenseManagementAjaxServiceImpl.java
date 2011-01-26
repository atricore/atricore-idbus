package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.idbus.console.services.spi.LicenseManagementAjaxService;
import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;
import com.atricore.idbus.console.services.spi.response.ActivateLicenseResponse;

/**
 * Author: Dejan Maric
 */
public class LicenseManagementAjaxServiceImpl implements LicenseManagementAjaxService {

    private LicenseManager licenseManager;

    public ActivateLicenseResponse activateLicense(ActivateLicenseRequest req) {
        ActivateLicenseResponse res = new ActivateLicenseResponse();
        try {
            licenseManager.activateLicense(req.getLicense().getValue());
        } catch (InvalidLicenseException e) {
            res.setErrorMsg("Invalid license file!");
        }
        return null;
    }

    public void setLicenseManager(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }
}
