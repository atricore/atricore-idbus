package com.atricore.idbus.console.lifecycle.main.domain.metadata;

import com.atricore.idbus.console.lifecycle.main.transform.annotations.ReEntrant;

import java.util.HashSet;
import java.util.Set;

@ReEntrant
public class DominoResource extends ServiceResource {

    private static final long serialVersionUID = -206643640681397571L;

    private Location homeLocation;

    private String serverUrl;

    private String version;

    public Location getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(Location homeLocation) {
        this.homeLocation = homeLocation;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
