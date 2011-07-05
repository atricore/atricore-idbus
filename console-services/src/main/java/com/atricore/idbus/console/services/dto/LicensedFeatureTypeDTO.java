package com.atricore.idbus.console.services.dto;

import com.atricore.josso2.licensing._1_0.license.FeatureType;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class LicensedFeatureTypeDTO implements Serializable {
    private static final long serialVersionUID = 475541230033855542L;

    protected List<FeatureTypeDTO> feature;

    public List<FeatureTypeDTO> getFeature() {
        if (feature == null) {
            feature = new ArrayList<FeatureTypeDTO>();
        }
        return this.feature;
    }

    public void setFeature(List<FeatureTypeDTO> feature) {
        this.feature = feature;
    }

}
