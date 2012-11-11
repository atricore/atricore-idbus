package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 *
 */
public class DominoAuthenticationService extends AuthenticationService {

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
