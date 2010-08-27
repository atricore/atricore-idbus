package com.atricore.idbus.console.services.dto;

import java.util.Set;

/**
 * Author: Dejan Maric
 */
public class JOSSOActivationDTO extends ActivationDTO {

    private static final long serialVersionUID = 3879493989758674128L;

    private String partnerAppId;

    private LocationDTO partnerAppLocation;

    private Set<String> ignoredWebResources;

    // TODO : Add other properties used to create JOSSO Agent config (PHP, ISAPI, Java, etc)


    public String getPartnerAppId() {
        return partnerAppId;
    }

    public void setPartnerAppId(String partnerAppId) {
        this.partnerAppId = partnerAppId;
    }

    public LocationDTO getPartnerAppLocation() {
        return partnerAppLocation;
    }

    public void setPartnerAppLocation(LocationDTO partnerAppLocation) {
        this.partnerAppLocation = partnerAppLocation;
    }

    public Set<String> getIgnoredWebResources() {
        return ignoredWebResources;
    }

    public void setIgnoredWebResources(Set<String> ignoredWebResources) {
        this.ignoredWebResources = ignoredWebResources;
    }
}
