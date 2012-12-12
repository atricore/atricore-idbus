package com.atricore.idbus.console.services.dto;

public class JBossEPPResourceDTO extends ServiceResourceDTO {

    private static final long serialVersionUID = 112839879734953533L;

    private LocationDTO partnerAppLocation;

    public LocationDTO getPartnerAppLocation() {
        return partnerAppLocation;
    }

    public void setPartnerAppLocation(LocationDTO partnerAppLocation) {
        this.partnerAppLocation = partnerAppLocation;
    }



}
