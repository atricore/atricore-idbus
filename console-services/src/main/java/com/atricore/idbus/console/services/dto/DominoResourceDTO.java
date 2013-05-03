package com.atricore.idbus.console.services.dto;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 4/30/13
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class DominoResourceDTO extends ServiceResourceDTO {

    private LocationDTO homeLocation;

    private String serverUrl;

    private String version;

    public LocationDTO getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(LocationDTO homeLocation) {
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
