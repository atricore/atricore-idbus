package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import com.atricore.idbus.console.lifecycle.main.transform.annotations.ReEntrant;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/25/13
 */
@ReEntrant
public class SelfServicesResource extends ServiceResource {

    private static final long serialVersionUID = -2066436406813938945l;

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
