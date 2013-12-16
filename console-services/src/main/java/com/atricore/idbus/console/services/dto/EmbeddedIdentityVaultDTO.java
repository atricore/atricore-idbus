package com.atricore.idbus.console.services.dto;

/**
 * Identity Source deployed as an embedded component on JOSSO.
 *
 * Author: Dejan Maric
 */
public class EmbeddedIdentityVaultDTO extends IdentityVaultDTO {

    private String identityConnectorName;

    public String getIdentityConnectorName() {
        return identityConnectorName;
    }

    public void setIdentityConnectorName(String identityConnectorName) {
        this.identityConnectorName = identityConnectorName;
    }

}
