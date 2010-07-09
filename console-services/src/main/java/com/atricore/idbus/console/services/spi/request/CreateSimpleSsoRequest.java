package com.atricore.idbus.console.services.spi.request;

import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityApplianceDefinition;
import org.atricore.idbus.capabilities.management.main.spi.request.AbstractManagementRequest;

/**
 * Author: Dejan Maric
 */
public class CreateSimpleSsoRequest extends AbstractManagementRequest {

    private IdentityApplianceDefinition identityApplianceDefinition;

    public IdentityApplianceDefinition getIdentityApplianceDefinition() {
        return identityApplianceDefinition;
    }

    public void setIdentityApplianceDefinition(IdentityApplianceDefinition identityApplianceDefinition) {
        this.identityApplianceDefinition = identityApplianceDefinition;
    }
}