package com.atricore.idbus.console.services.dto;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Dejan Maric
 */
public class JOSSOActivationDTO extends ActivationDTO {

    private static final long serialVersionUID = 3879493989758674128L;

    // TODO: Remove me!!! [JOSSO-370]
    private String partnerAppId;

    // TODO: Remove me!!! [JOSSO-370]
    private LocationDTO partnerAppLocation;

    // TODO: Remove me!!! [JOSSO-370]
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
        if(ignoredWebResources == null){
            ignoredWebResources = new HashSet<String>();
        }        
        return ignoredWebResources;
    }

    public void setIgnoredWebResources(Set<String> ignoredWebResources) {
        this.ignoredWebResources = ignoredWebResources;
    }
}
