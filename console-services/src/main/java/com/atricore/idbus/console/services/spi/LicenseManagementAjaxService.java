package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.request.ActivateLicenseRequest;
import com.atricore.idbus.console.services.spi.response.ActivateLicenseResponse;

/**
 * Author: Dejan Maric
 */
public interface LicenseManagementAjaxService {

    ActivateLicenseResponse activateLicense(ActivateLicenseRequest req);

}
