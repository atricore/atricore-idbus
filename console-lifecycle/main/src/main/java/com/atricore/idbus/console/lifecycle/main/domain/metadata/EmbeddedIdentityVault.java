package com.atricore.idbus.console.lifecycle.main.domain.metadata;

/**
 * Identity Source deployed as an Identity Connector (OSGi bundle), no part of the appliance
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EmbeddedIdentityVault extends IdentityVault {

    private static final long serialVersionUID = 556620203559177763L;

    private String identityConnectorName;

    public String getIdentityConnectorName() {
        return identityConnectorName;
    }

    public void setIdentityConnectorName(String identityConnectorName) {
        this.identityConnectorName = identityConnectorName;
    }

}
