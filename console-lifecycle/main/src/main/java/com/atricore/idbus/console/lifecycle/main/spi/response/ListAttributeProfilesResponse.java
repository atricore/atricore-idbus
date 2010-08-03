package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AttributeProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAttributeProfilesResponse {

    private List<AttributeProfile> attributeProfiles;

    public List<AttributeProfile> getAttributeProfiles() {
        if(attributeProfiles == null){
            attributeProfiles = new ArrayList<AttributeProfile>();
        }
        return attributeProfiles;
    }

    public void setAttributeProfiles(List<AttributeProfile> attributeProfiles) {
        this.attributeProfiles = attributeProfiles;
    }
}
