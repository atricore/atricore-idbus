package com.atricore.idbus.console.services.dto;

/**
 *
 */
public class DominoAuthenticationServiceDTO extends AuthenticationServiceDTO {

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
