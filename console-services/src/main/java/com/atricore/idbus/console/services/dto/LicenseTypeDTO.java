package com.atricore.idbus.console.services.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class LicenseTypeDTO implements Serializable {
    private static final long serialVersionUID = 475540870033858942L;
    private OrganizationTypeDTO organization;
    private Date issueInstant;

    private List<LicensedFeatureTypeDTO> licensedFeature;

    protected String eula;    

    public OrganizationTypeDTO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationTypeDTO organization) {
        this.organization = organization;
    }

    public List<LicensedFeatureTypeDTO> getLicensedFeature() {
        if(licensedFeature == null){
            licensedFeature = new ArrayList<LicensedFeatureTypeDTO>();
        }
        return licensedFeature;
    }

    public void setLicensedFeature(List<LicensedFeatureTypeDTO> licensedFeature) {
        this.licensedFeature = licensedFeature;
    }

    public Date getIssueInstant() {
        return issueInstant;
    }

    public void setIssueInstant(Date issueInstant) {
        this.issueInstant = issueInstant;
    }

    public String getEula() {
        return eula;
    }

    public void setEula(String eula) {
        this.eula = eula;
    }
}
