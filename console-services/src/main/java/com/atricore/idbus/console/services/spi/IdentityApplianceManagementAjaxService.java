package com.atricore.idbus.console.services.spi;

import org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;

/**
 * Author: Dejan Maric
 */
public interface IdentityApplianceManagementAjaxService {

	DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;

	UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException;

    ImportIdentityApplianceResponse importIdentityAppliance(ImportIdentityApplianceRequest request) throws IdentityServerException;

    ExportIdentityApplianceResponse ExportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException;

    ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException;
    
    // D -> DEPLOYED -> STARTED -> STOPPED -> UNDEPLOYED(D)

    CreateSimpleSsoResponse createSimpleSso(CreateSimpleSsoRequest req) throws IdentityServerException;

    AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException;

    LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException;

    RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException;

    AddIdentityApplianceDefinitionResponse addIdentityApplianceDefinition(AddIdentityApplianceDefinitionRequest req) throws IdentityServerException;

    UpdateApplianceDefinitionResponse updateApplianceDefinition(UpdateApplianceDefinitionRequest request) throws IdentityServerException;

    LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest request) throws IdentityServerException;

    LookupIdentityApplianceDefinitionResponse lookupIdentityApplianceDefinition(LookupIdentityApplianceDefinitionRequest request) throws IdentityServerException;

    ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException;
    
}
