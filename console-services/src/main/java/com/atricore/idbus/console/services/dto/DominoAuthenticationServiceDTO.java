package com.atricore.idbus.console.services.dto;

/**
 *
 */
public class DominoAuthenticationServiceDTO extends AuthenticationServiceDTO {

    private String serverUrl;

    private String version;

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
