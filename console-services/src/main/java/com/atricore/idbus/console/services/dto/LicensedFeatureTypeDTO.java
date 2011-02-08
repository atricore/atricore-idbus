package com.atricore.idbus.console.services.dto;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.Date;

/**
 * Author: Dejan Maric
 */
public class LicensedFeatureTypeDTO implements Serializable {
    private static final long serialVersionUID = 475541230033855542L;

    protected FeatureTypeDTO feature;    
    protected Date issueDate;
    protected Date expirationDate;

    public FeatureTypeDTO getFeature() {
        return feature;
    }

    public void setFeature(FeatureTypeDTO feature) {
        this.feature = feature;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
