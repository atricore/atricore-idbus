/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.services.spi;

import com.atricore.idbus.console.services.spi.request.*;
import com.atricore.idbus.console.services.spi.response.*;

/**
 * Author: Dejan Maric
 */
public interface IdentityApplianceManagementAjaxService {

    BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest req) throws IdentityServerException;

	DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException;

	UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException;

    StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException;

    StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException;

    DisposeIdentityApplianceResponse disposeIdentityAppliance(DisposeIdentityApplianceRequest req) throws IdentityServerException;
    
    ExportIdentityApplianceResponse ExportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException;

    ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException;

    ActivateExecEnvResponse activateExecEnv(ActivateExecEnvRequest request) throws IdentityServerException;
    
    // D -> DEPLOYED -> STARTED -> STOPPED -> UNDEPLOYED(D)

    CreateSimpleSsoResponse createSimpleSso(CreateSimpleSsoRequest req) throws IdentityServerException;

    AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException;

    UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException;    

    LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException;

    ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest request) throws IdentityServerException;
    
    RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException;

    CheckInstallFolderExistenceResponse checkInstallFolderExistence(CheckInstallFolderExistenceRequest req) throws IdentityServerException;

    CheckFoldersExistenceResponse checkFoldersExistence(CheckFoldersExistenceRequest req) throws IdentityServerException;
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

    ExportProviderCertificateResponse exportProviderCertificate(ExportProviderCertificateRequest req) throws IdentityServerException;
}
