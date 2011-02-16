package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.idbus.console.services.dto.LicenseTypeDTO;
import com.atricore.idbus.console.services.spi.LicenseManagementAjaxService;
import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;
import com.atricore.idbus.console.services.spi.request.GetLicenseRequest;
import com.atricore.idbus.console.services.spi.response.ActivateLicenseResponse;
import com.atricore.idbus.console.services.spi.response.GetLicenseResponse;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class LicenseManagementAjaxServiceImpl implements LicenseManagementAjaxService {

    private LicenseManager licenseManager;
    private DozerBeanMapper dozerMapper;

    public ActivateLicenseResponse activateLicense(ActivateLicenseRequest req) {
        ActivateLicenseResponse res = new ActivateLicenseResponse();
        try {
            licenseManager.activateLicense(req.getLicense().getValue());
        } catch (InvalidLicenseException e) {
            res.setErrorMsg("Invalid license file!");
        }
        return res;
    }

    public GetLicenseResponse getLicense(GetLicenseRequest req) {
        GetLicenseResponse res = new GetLicenseResponse();
        try {
            licenseManager.getLicense();
            res.setLicense(dozerMapper.map(licenseManager.getLicense(), LicenseTypeDTO.class));
        } catch (InvalidLicenseException e) {
            res.setError("Error loading license.");
        }
        //TODO implement
        return res;
    }


    public void setLicenseManager(LicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public void setDozerMapper(DozerBeanMapper dozerMapper) {
        this.dozerMapper = dozerMapper;
    }
}
