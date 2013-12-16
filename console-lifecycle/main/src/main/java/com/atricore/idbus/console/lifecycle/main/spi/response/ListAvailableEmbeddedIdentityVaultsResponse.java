package com.atricore.idbus.console.lifecycle.main.spi.response;

import java.util.List;
import org.atricore.idbus.kernel.main.provisioning.spi.IdentityConnector;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ListAvailableEmbeddedIdentityVaultsResponse extends AbstractManagementResponse {

    private List<IdentityConnector> embeddedIdentityVaults;

    public List<IdentityConnector> getEmbeddedIdentityVaults() {
        return embeddedIdentityVaults;
    }

    public void setEmbeddedIdentityVaults(List<IdentityConnector> embeddedIdentityVaults) {
        this.embeddedIdentityVaults = embeddedIdentityVaults;
    }
}
