package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class JBossEPPResource extends ServiceResource {

    private static final long serialVersionUID = 112232379734953533L;

    private Location partnerAppLocation;

    public Location getPartnerAppLocation() {
        return partnerAppLocation;
    }

    public void setPartnerAppLocation(Location partnerAppLocation) {
        this.partnerAppLocation = partnerAppLocation;
    }

}
