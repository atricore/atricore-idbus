package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.idbus.console.licensing.main.LicenseServiceError;
import com.atricore.idbus.console.services.dto.LicenseTypeDTO;
import com.atricore.idbus.console.services.spi.LicenseManagementAjaxService;
import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;
import com.atricore.idbus.console.services.spi.request.GetLicenseRequest;
import com.atricore.idbus.console.services.spi.request.ValidateLicenseRequest;
import com.atricore.idbus.console.services.spi.response.ActivateLicenseResponse;
import com.atricore.idbus.console.services.spi.response.GetLicenseResponse;
import com.atricore.idbus.console.services.spi.response.ValidateLicenseResponse;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.dozer.DozerBeanMapper;

/**
 * Author: Dejan Maric
 */
public class LicenseManagementAjaxServiceImpl implements LicenseManagementAjaxService {

    private LicenseManager licenseManager;
    private DozerBeanMapper dozerMapper;


    public ValidateLicenseResponse validateLicense(ValidateLicenseRequest req) {
        ValidateLicenseResponse res = new ValidateLicenseResponse();
        try {
            licenseManager.validateLicense(req.getLicense().getValue());
            res.setValid(true);
        } catch (InvalidLicenseException e) {
            res.setValid(false);
        } catch (LicenseServiceError e) {
            res.setErrorMsg(e.getMessage());
        }
        return res;
    }

    public ActivateLicenseResponse activateLicense(ActivateLicenseRequest req) {
        ActivateLicenseResponse res = new ActivateLicenseResponse();
        try {
            licenseManager.activateLicense(req.getLicense().getValue());
            res.setValid(true);
        } catch (InvalidLicenseException e) {
            res.setValid(false);
        } catch (LicenseServiceError e) {
            res.setErrorMsg(e.getMessage());
        }
        return res;
    }

    public GetLicenseResponse getLicense(GetLicenseRequest req) {
        GetLicenseResponse res = new GetLicenseResponse();
        try {
            LicenseType license = licenseManager.getCurrentLicense();
            res.setLicense(dozerMapper.map(license, LicenseTypeDTO.class));
            res.setValid(true);
        } catch (InvalidLicenseException e) {
            res.setValid(false);
        } catch (Exception e) {
            res.setError(e.getMessage());
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
