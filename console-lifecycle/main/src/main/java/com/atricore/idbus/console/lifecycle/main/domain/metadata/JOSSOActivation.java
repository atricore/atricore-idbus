package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class JOSSOActivation extends Activation {

    private static final long serialVersionUID = 3879493989758674128L;

    // TODO: Remove me!!! [JOSSO-370]
    private String partnerAppId;

    // TODO: Remove me!!! [JOSSO-370]
    private Location partnerAppLocation;

    // TODO: Remove me!!! [JOSSO-370]
    private Set<String> ignoredWebResources;

    // TODO : Add other properties used to create JOSSO Agent config (PHP, ISAPI, Java, etc)
    // TODO : Add ACS location, SLO location (SEE JOSSOActivationTransformer)

    public String getPartnerAppId() {
        return partnerAppId;
    }

    public void setPartnerAppId(String partnerAppId) {
        this.partnerAppId = partnerAppId;
    }

    public Location getPartnerAppLocation() {
        return partnerAppLocation;
    }

    public void setPartnerAppLocation(Location partnerAppLocation) {
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
