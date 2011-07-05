package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;
import com.atricore.idbus.console.services.spi.request.GetLicenseRequest;
import com.atricore.idbus.console.services.spi.request.ValidateLicenseRequest;
import com.atricore.idbus.console.services.spi.response.ActivateLicenseResponse;
import com.atricore.idbus.console.services.spi.response.GetLicenseResponse;
import com.atricore.idbus.console.services.spi.response.ValidateLicenseResponse;

/**
 * Author: Dejan Maric
 */
public interface LicenseManagementAjaxService {

    ValidateLicenseResponse validateLicense(ValidateLicenseRequest req);

    ActivateLicenseResponse activateLicense(ActivateLicenseRequest req);

    GetLicenseResponse getLicense(GetLicenseRequest req);

}
