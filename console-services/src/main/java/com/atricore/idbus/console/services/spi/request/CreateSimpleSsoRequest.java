package com.atricore.idbus.console.services.spi.request;

import com.atricore.idbus.console.services.dto.IdentityApplianceDefinitionDTO;
import org.atricore.idbus.capabilities.management.main.spi.request.AbstractManagementRequest;

/**
 * Author: Dejan Maric
 */
public class CreateSimpleSsoRequest extends AbstractManagementRequest {

    private IdentityApplianceDefinitionDTO identityApplianceDefinition;

    public IdentityApplianceDefinitionDTO getIdentityApplianceDefinition() {
        return identityApplianceDefinition;
    }

    public void setIdentityApplianceDefinition(IdentityApplianceDefinitionDTO identityApplianceDefinition) {
        this.identityApplianceDefinition = identityApplianceDefinition;
    }
}