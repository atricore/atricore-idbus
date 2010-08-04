package com.atricore.idbus.console.lifecycle.main.spi.response;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class AddIdentityApplianceDefinitionResponse extends AbstractManagementResponse {

    private IdentityApplianceDefinition identityApplianceDefinition;

    public AddIdentityApplianceDefinitionResponse() {
        super();
    }

    public AddIdentityApplianceDefinitionResponse(IdentityApplianceDefinition identityApplianceDefinition) {
        this.identityApplianceDefinition = identityApplianceDefinition;
    }

    public IdentityApplianceDefinition getIdentityApplianceDefinition() {
        return identityApplianceDefinition;
    }

    public void setIdentityApplianceDefinition(IdentityApplianceDefinition identityApplianceDefinition) {
        this.identityApplianceDefinition = identityApplianceDefinition;
    }
}
