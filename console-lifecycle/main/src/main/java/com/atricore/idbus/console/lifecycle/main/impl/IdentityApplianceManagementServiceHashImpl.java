/*
 * Atricore IDBus
 *
 * Copyright 2009-2010, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.*;
import com.atricore.idbus.console.lifecycle.main.spi.response.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class IdentityApplianceManagementServiceHashImpl
    implements IdentityApplianceManagementService
{

    static Log logger = LogFactory.getLog(IdentityApplianceManagementServiceHashImpl.class);

    private static Map<Long, IdentityAppliance> appliances = new HashMap<Long, IdentityAppliance>();


    public BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest request) throws IdentityServerException {
        return null;
    }

    public DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException {
        return null;
    }

    public UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException {
        return null;
    }

    public StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException {
        return null;
    }

    public StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException {
        return null;
    }

    public ImportIdentityApplianceResponse importIdentityAppliance(ImportIdentityApplianceRequest request) throws IdentityServerException {
        return null;
    }

    public ExportIdentityApplianceResponse exportIdentityAppliance(ExportIdentityApplianceRequest request) throws IdentityServerException {
        return null;
    }

    public ImportApplianceDefinitionResponse importApplianceDefinition(ImportApplianceDefinitionRequest request) throws IdentityServerException {
        return null;
    }

    public ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException {
        return null;
    }

    public AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException {
        AddIdentityApplianceResponse res = null;
        try {
            IdentityAppliance appliance = req.getIdentityAppliance();

            if (appliance.getIdApplianceDefinition() == null)
                throw new IdentityServerException("Appliances must contain an appliance definition!");

            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
            applianceDef.setRevision(1);
            applianceDef.setLastModification(new Date());

            if (appliance.getId() <= 0) {
                appliance.setId(System.nanoTime());
            }

            appliances.put(appliance.getId(), appliance);

            res = new AddIdentityApplianceResponse();
            res.setAppliance(appliance);

        } catch (Exception e){
	        logger.error("Error adding identity appliance", e);
	        throw new IdentityServerException(e);
	    }
        return res;
    }

    public UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException {
        UpdateIdentityApplianceResponse res = null;
        try {
            IdentityAppliance appliance = request.getAppliance();

            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

            applianceDef.setLastModification(new Date());
            applianceDef.setRevision(applianceDef.getRevision() + 1);

            appliances.put(appliance.getId(), appliance);

            res = new UpdateIdentityApplianceResponse();
            res.setAppliance(appliance);

        } catch (Exception e){
	        logger.error("Error updating identity appliance", e);
	        throw new IdentityServerException(e);
	    }
        return res;
    }

    public LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException {
        LookupIdentityApplianceByIdResponse res = null;
        try {
            IdentityAppliance appliance = appliances.get(Long.parseLong(request.getIdentityApplianceId()));
            res = new LookupIdentityApplianceByIdResponse();
            res.setIdentityAppliance(appliance);
        } catch (Exception e){
	        logger.error("Error looking for identity appliance", e);
	        throw new IdentityServerException(e);
	    }
        return res;
    }

    public RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException {
        try {
            if (req.getIdentityAppliance() != null) {
                appliances.remove(req.getIdentityAppliance().getId());
            }
            RemoveIdentityApplianceResponse res = new RemoveIdentityApplianceResponse();
            return res;
        } catch (Exception e){
	        logger.error("Error removing identity appliance", e);
	        throw new IdentityServerException(e);
	    }
    }

    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException {
        return null;
    }

    public ListIdentityAppliancesByStateResponse listIdentityAppliancesByState(ListIdentityAppliancesByStateRequest req) throws IdentityServerException {
        return null;
    }

    public AddIdentityApplianceDefinitionResponse addIdentityApplianceDefinition(AddIdentityApplianceDefinitionRequest req) throws IdentityServerException {
        return null;
    }

    public LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest request) throws IdentityServerException {
        return null;
    }

    public ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException {
        return null;
    }

    public ListIdentityVaultsResponse listIdentityVaults(ListIdentityVaultsRequest req) throws IdentityServerException {
        return null;
    }

    public ListUserInformationLookupsResponse listUserInformationLookups(ListUserInformationLookupsRequest req) throws IdentityServerException {
        return null;
    }

    public ListAccountLinkagePoliciesResponse listAccountLinkagePolicies(ListAccountLinkagePoliciesRequest req) throws IdentityServerException {
        return null;
    }

    public ListAuthenticationContractsResponse listAuthenticationContracts(ListAuthenticationContractsRequest req) throws IdentityServerException {
        return null;
    }

    public ListAuthenticationMechanismsResponse listAuthenticationMechanisms(ListAuthenticationMechanismsRequest req) throws IdentityServerException {
        return null;
    }

    public ListAttributeProfilesResponse listAttributeProfiles(ListAttributeProfilesRequest req) throws IdentityServerException {
        return null;
    }

    public ListAuthAssertionEmissionPoliciesResponse listAuthAssertionEmissionPolicies(ListAuthAssertionEmissionPoliciesRequest req) throws IdentityServerException {
        return null;
    }

    public LookupIdentityVaultByIdResponse lookupIdentityVaultById(LookupIdentityVaultByIdRequest req) throws IdentityServerException {
        return null;
    }

    public LookupUserInformationLookupByIdResponse lookupUserInformationLookupById(LookupUserInformationLookupByIdRequest req) throws IdentityServerException {
        return null;
    }

    public LookupAccountLinkagePolicyByIdResponse lookupAccountLinkagePolicyById(LookupAccountLinkagePolicyByIdRequest req) throws IdentityServerException {
        return null;
    }

    public LookupAuthenticationContractByIdResponse lookupAuthenticationContractById(LookupAuthenticationContractByIdRequest req) throws IdentityServerException {
        return null;
    }

    public LookupAuthenticationMechanismByIdResponse lookupAuthenticationMechanismById(LookupAuthenticationMechanismByIdRequest req) throws IdentityServerException {
        return null;
    }

    public LookupAttributeProfileByIdResponse lookupAttributeProfileById(LookupAttributeProfileByIdRequest req) throws IdentityServerException {
        return null;
    }

    public LookupAuthAssertionEmissionPolicyByIdResponse lookupAuthAssertionEmissionPolicyById(LookupAuthAssertionEmissionPolicyByIdRequest req) throws IdentityServerException {
        return null;
    }

    public AddResourceResponse addResource(AddResourceRequest req) throws IdentityServerException {
        return null;
    }

    public LookupResourceByIdResponse lookupResourceById(LookupResourceByIdRequest req) throws IdentityServerException {
        return null;
    }
}