package com.atricore.idbus.console.lifecycle.main.spi;

import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.request.*;
import com.atricore.idbus.console.lifecycle.main.spi.response.*;


public interface IdentityApplianceManagementService {

    void boot() throws IdentityServerException;

    //-----------------------< Lifecycle operations >

    /**
     * Builds the Identity Appliance artifact, it will also be installed.
     *
     * @param request
     * @return
     * @throws IdentityServerException
     */
    BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest request) throws IdentityServerException;

    DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException;
    
	UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException;

    StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException;

	StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException;

    DisposeIdentityApplianceResponse disposeIdentityAppliance(DisposeIdentityApplianceRequest req) throws IdentityServerException;

    ExportIdentityApplianceResponse exportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException;

    ImportIdentityApplianceResponse importIdentityApplianceProject(ImportIdentityApplianceRequest request) throws IdentityServerException;

    ImportApplianceDefinitionResponse importApplianceDefinition(ImportApplianceDefinitionRequest request) throws IdentityServerException;

    ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException;

    ActivateExecEnvResponse activateExecEnv(ActivateExecEnvRequest request) throws IdentityServerException;

    ValidateApplianceResponse validateApplinace(ValidateApplianceRequest request) throws IdentityServerException;

    ExportIdentityApplianceProjectResponse exportIdentityApplianceProject(ExportIdentityApplianceProjectRequest request) throws IdentityServerException;

    //-----------------------< CRUD Operations >

    /**
     * Adds new Identity Appliance
     */
    AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException;

    UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException;

    LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException;

    RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException;

    ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException;

    ListIdentityAppliancesByStateResponse listIdentityAppliancesByState(ListIdentityAppliancesByStateRequest req) throws IdentityServerException;

    /**
     * Look up an identity bus instance based on its id.
     */
    LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest request) throws IdentityServerException;
    ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException;

    /****************************
     * List methods
     ***************************/
    ListAvailableJDBCDriversResponse listAvailableJDBCDrivers(ListAvailableJDBCDriversRequest request) throws IdentityServerException;
    ListIdentityVaultsResponse listIdentityVaults(ListIdentityVaultsRequest req) throws IdentityServerException;
    ListUserInformationLookupsResponse listUserInformationLookups(ListUserInformationLookupsRequest req) throws IdentityServerException;
    ListAccountLinkagePoliciesResponse listAccountLinkagePolicies(ListAccountLinkagePoliciesRequest req) throws IdentityServerException;
    ListAuthenticationContractsResponse listAuthenticationContracts(ListAuthenticationContractsRequest req) throws IdentityServerException;
    ListAuthenticationMechanismsResponse listAuthenticationMechanisms(ListAuthenticationMechanismsRequest req) throws IdentityServerException;
    ListAttributeProfilesResponse listAttributeProfiles(ListAttributeProfilesRequest req) throws IdentityServerException;
    ListAuthAssertionEmissionPoliciesResponse listAuthAssertionEmissionPolicies(ListAuthAssertionEmissionPoliciesRequest req) throws IdentityServerException;

    /****************************
     * Lookup methods
     ***************************/
    LookupIdentityVaultByIdResponse lookupIdentityVaultById(LookupIdentityVaultByIdRequest req) throws IdentityServerException;
    LookupUserInformationLookupByIdResponse lookupUserInformationLookupById(LookupUserInformationLookupByIdRequest req) throws IdentityServerException;
    LookupAccountLinkagePolicyByIdResponse lookupAccountLinkagePolicyById(LookupAccountLinkagePolicyByIdRequest req) throws IdentityServerException;
    LookupAuthenticationContractByIdResponse lookupAuthenticationContractById(LookupAuthenticationContractByIdRequest req) throws IdentityServerException;
    LookupAuthenticationMechanismByIdResponse lookupAuthenticationMechanismById(LookupAuthenticationMechanismByIdRequest req) throws IdentityServerException;
    LookupAttributeProfileByIdResponse lookupAttributeProfileById(LookupAttributeProfileByIdRequest req) throws IdentityServerException;
    LookupAuthAssertionEmissionPolicyByIdResponse lookupAuthAssertionEmissionPolicyById(LookupAuthAssertionEmissionPolicyByIdRequest req) throws IdentityServerException;

    AddResourceResponse addResource(AddResourceRequest req) throws IdentityServerException;
    LookupResourceByIdResponse lookupResourceById(LookupResourceByIdRequest req) throws IdentityServerException;

    GetMetadataInfoResponse getMetadataInfo(GetMetadataInfoRequest req) throws IdentityServerException;
    GetCertificateInfoResponse getCertificateInfo(GetCertificateInfoRequest req) throws IdentityServerException;
}
