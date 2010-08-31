package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.AttributeProfile;

/**
 * Author: Dejan Maric
 */
public class LookupAttributeProfileByIdResponse {

    private AttributeProfile attributeProfile;

    public AttributeProfile getAttributeProfile() {
        return attributeProfile;
    }

    public void setAttributeProfile(AttributeProfile attributeProfile) {
        this.attributeProfile = attributeProfile;
    }
}
