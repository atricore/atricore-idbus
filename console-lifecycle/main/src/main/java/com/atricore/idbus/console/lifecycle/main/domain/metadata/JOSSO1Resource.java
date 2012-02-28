package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import java.util.HashSet;
import java.util.Set;

public class JOSSO1Resource extends ServiceResource {

    private static final long serialVersionUID = -206643640681397571L;

    private String partnerAppId;

    private Location partnerAppLocation;

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
        if (ignoredWebResources == null) {
            ignoredWebResources = new HashSet<String>();
        }
        return ignoredWebResources;
    }

    public void setIgnoredWebResources(Set<String> ignoredWebResources) {
        this.ignoredWebResources = ignoredWebResources;
    }
}
