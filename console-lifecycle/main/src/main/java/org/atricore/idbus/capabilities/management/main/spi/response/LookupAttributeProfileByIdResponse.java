package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.AttributeProfile;

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
