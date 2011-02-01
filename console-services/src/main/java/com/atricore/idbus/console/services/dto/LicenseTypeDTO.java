package com.atricore.idbus.console.services.dto;

/**
 * Author: Dejan Maric
 */
public class LicenseTypeDTO {

    private String licensedTo;

    /*
    protected List<LicensedFeatureType> licensedFeature;
    protected XMLGregorianCalendar issueInstant;
    * */

    public String getLicensedTo() {
        return licensedTo;
    }

    public void setLicensedTo(String licensedTo) {
        this.licensedTo = licensedTo;
    }
}
