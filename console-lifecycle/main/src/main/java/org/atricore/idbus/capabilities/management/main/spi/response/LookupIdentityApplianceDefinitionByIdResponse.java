package org.atricore.idbus.capabilities.management.main.spi.response;

import org.atricore.idbus.capabilities.management.main.domain.metadata.IdentityApplianceDefinition;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LookupIdentityApplianceDefinitionByIdResponse extends AbstractManagementResponse {

    private IdentityApplianceDefinition identityApplianceDefinition;    

    public IdentityApplianceDefinition getIdentityApplianceDefinition() {
        return identityApplianceDefinition;
    }

    public void setIdentityApplianceDefinition(IdentityApplianceDefinition identityApplianceDefinition) {
        this.identityApplianceDefinition = identityApplianceDefinition;
    }

    public LookupIdentityApplianceDefinitionByIdResponse() {
        super();
    }


}
