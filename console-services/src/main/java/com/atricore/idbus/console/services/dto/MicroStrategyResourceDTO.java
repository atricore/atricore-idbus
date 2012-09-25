package com.atricore.idbus.console.services.dto;

public class MicroStrategyResourceDTO extends ServiceResourceDTO {

    private static final long serialVersionUID = 5422113556831482988L;

    private String secret;

    private LocationDTO location;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }


}
