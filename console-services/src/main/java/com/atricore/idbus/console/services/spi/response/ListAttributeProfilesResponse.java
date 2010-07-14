package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AttributeProfileDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Dejan Maric
 */
public class ListAttributeProfilesResponse {

    private List<AttributeProfileDTO> attributeProfiles;

    public List<AttributeProfileDTO> getAttributeProfiles() {
        if(attributeProfiles == null){
            attributeProfiles = new ArrayList<AttributeProfileDTO>();
        }
        return attributeProfiles;
    }

    public void setAttributeProfiles(List<AttributeProfileDTO> attributeProfiles) {
        this.attributeProfiles = attributeProfiles;
    }
}
