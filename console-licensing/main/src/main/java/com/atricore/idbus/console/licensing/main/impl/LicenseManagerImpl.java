package com.atricore.idbus.console.licensing.main.impl;

import com.atricore.idbus.console.licensing.main.InvalidFeatureException;
import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.josso2.licensing._1_0.license.LicenseType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseManagerImpl implements LicenseManager {

    // Enconded digital certifiate (in the future, we should be able to manage a set of certs).
    private String licenseCertificate = "";

    public void activateLicense(byte[] license) throws InvalidLicenseException {
        // 1. Unmarshal and call validate again
    }

    public void activateLicense(LicenseType license) throws InvalidLicenseException {
        // 1. Validate signature
        // 2. Store license file in etc (DB in the future ?)
    }

    public void validateLicense() throws InvalidLicenseException {

    }

    public void validateFeature(String group, String name) throws InvalidFeatureException {

    }

    public LicenseType getLicense() {
        return null;
    }

    protected void validateSignature(LicenseType license, String certificate) throws InvalidLicenseException {

    }
}
