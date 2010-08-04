package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LookupIdentityApplianceDefinitionResponse extends AbstractManagementResponse {

    private IdentityApplianceDefinition identityAppliance;

    public IdentityApplianceDefinition getIdentityAppliance() {
        return identityAppliance;
    }

    public void setIdentityAppliance(IdentityApplianceDefinition identityAppliance) {
        this.identityAppliance = identityAppliance;
    }
}
