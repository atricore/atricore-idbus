package com.atricore.idbus.console.licensing.main;

import com.atricore.josso2.licensing._1_0.license.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface LicenseManager {

    /**
     * Activates binary license representation.  This is a base64 value of the unmarshalled license XML file.
     */
    void activateLicense(byte[] license) throws InvalidLicenseException;

    /**
     * Activates license
     */
    void activateLicense(LicenseType license) throws InvalidLicenseException;

    /**
     * Validate current license, the last one activated.
     * @throws InvalidLicenseException
     */
    void validateLicense() throws InvalidLicenseException;

    /**
     * Check if a feature is valid in the current license.
     */
    void validateFeature(String group, String name) throws InvalidFeatureException;

    /**
     * Retrive active license information
     */
    LicenseType getLicense();

}
