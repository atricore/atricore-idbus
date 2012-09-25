package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public class MicroStrategyResource extends ServiceResource {

    private static final long serialVersionUID = -7991824895038241799L;

    private String secret;

    private Location location;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
