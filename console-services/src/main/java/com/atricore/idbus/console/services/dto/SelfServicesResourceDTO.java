package com.atricore.idbus.console.services.dto;

import com.atricore.idbus.console.lifecycle.main.transform.transformers.mstr.MstrResoruceTransformer;

import java.io.Serializable;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 2/25/13
 */
public class SelfServicesResourceDTO extends ServiceResourceDTO implements Serializable {

    private static final long serialVersionUID = 1094595468432478819L;

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
