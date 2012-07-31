package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import com.atricore.idbus.console.lifecycle.main.transform.annotations.ReEntrant;

import java.util.HashSet;
import java.util.Set;

// This work very much like connections, make elemets re-entrant
@ReEntrant
public class JOSSO1Resource extends ServiceResource {

    private static final long serialVersionUID = -206643640681397571L;

    private Location partnerAppLocation;

    private Set<String> ignoredWebResources;

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
