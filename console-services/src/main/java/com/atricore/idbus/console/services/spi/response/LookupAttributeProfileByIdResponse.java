package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.AttributeProfileDTO;

/**
 * Author: Dejan Maric
 */
public class LookupAttributeProfileByIdResponse {

    private AttributeProfileDTO attributeProfile;

    public AttributeProfileDTO getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfileDTO attributeProfile) {
        this.attributeProfile = attributeProfile;
    }
}
