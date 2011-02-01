package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;
import com.atricore.idbus.console.services.spi.request.GetLicenseRequest;
import com.atricore.idbus.console.services.spi.response.ActivateLicenseResponse;
import com.atricore.idbus.console.services.spi.response.GetLicenseResponse;

/**
 * Author: Dejan Maric
 */
public interface LicenseManagementAjaxService {

    ActivateLicenseResponse activateLicense(ActivateLicenseRequest req);
    GetLicenseResponse getLicense(GetLicenseRequest req);

}
