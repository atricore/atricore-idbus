package com.atricore.idbus.console.lifecycle.main.spi.request;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class LookupIdentityApplianceDefinitionByIdRequest extends AbstractManagementRequest {

    private String identityApplianceDefinitionId;

    public String getIdentityApplianceDefinitionId() {
        return identityApplianceDefinitionId;
    }

    public void setIdentityApplianceDefinitionId(String identityApplianceDefinitionId) {
        this.identityApplianceDefinitionId = identityApplianceDefinitionId;
    }
}
