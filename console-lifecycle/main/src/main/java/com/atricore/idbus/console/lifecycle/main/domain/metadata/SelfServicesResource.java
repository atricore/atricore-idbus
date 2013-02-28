package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/25/13
 */
public class SelfServicesResource extends ServiceResource {

    // TODO : Maybe we need a parent OAUTH resource, instead of JOSSO 2 ?!

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
