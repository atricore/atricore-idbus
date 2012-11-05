package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 *
 */
public class DominoAuthenticationService extends AuthenticationService {

    private String registryUrl;

    private String version;

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
