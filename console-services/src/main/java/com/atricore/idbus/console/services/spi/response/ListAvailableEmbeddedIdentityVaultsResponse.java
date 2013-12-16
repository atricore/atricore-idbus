package com.atricore.idbus.console.services.spi.response;

import com.atricore.idbus.console.services.dto.IdentityConnectorDTO;

import java.util.List;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListAvailableEmbeddedIdentityVaultsResponse {

    private List<IdentityConnectorDTO> embeddedIdentityVaults;

    public List<IdentityConnectorDTO> getEmbeddedIdentityVaults() {
        return embeddedIdentityVaults;
    }

    public void setEmbeddedIdentityVaults(List<IdentityConnectorDTO> embeddedIdentityVaults) {
        this.embeddedIdentityVaults = embeddedIdentityVaults;
    }
}
