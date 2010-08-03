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

package com.atricore.idbus.console.lifecycle.main.impl;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnit;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceUnitType;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.exception.IdentityServerException;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.StatusCode;
import com.atricore.idbus.console.lifecycle.main.spi.request.*;
import com.atricore.idbus.console.lifecycle.main.spi.response.*;

import javax.jdo.*;

@Deprecated
public class IdentityApplianceManagementServiceMockImpl implements
        IdentityApplianceManagementService {

    private static final Log logger = LogFactory.getLog(IdentityApplianceManagementServiceMockImpl.class);
    
    private PersistenceManagerFactory pmf;

	private List<IdentityAppliance> identityAppliances;
	
	public IdentityApplianceManagementServiceMockImpl() {
		identityAppliances = new ArrayList<IdentityAppliance>();
		
		IdentityAppliance identityAppliance1 = new IdentityAppliance();
    	identityAppliance1.setId(1);
    	IdentityApplianceDefinition iad1 = new IdentityApplianceDefinition();
    	iad1.setName("Started appliance 1");
    	iad1.setDescription("Description 1");
    	Location location1 = new Location();
    	location1.setProtocol("http");
    	location1.setHost("host1.com");
    	location1.setPort(8080);
    	location1.setContext("context1");
    	location1.setUri("uri1");
    	iad1.setLocation(location1);
    	identityAppliance1.setIdApplianceDefinition(iad1);
    	IdentityApplianceDeployment iaDeployment1 = new IdentityApplianceDeployment();
    	iaDeployment1.setState("Started");
    	Set<IdentityApplianceUnit> idaus1 = new LinkedHashSet<IdentityApplianceUnit>();
    	IdentityApplianceUnit iau1 = new IdentityApplianceUnit();
    	iau1.setName("Unit 1");
    	iau1.setType(IdentityApplianceUnitType.FEDERATION_UNIT);
        List<Provider> providers1 = new ArrayList<Provider>();
        IdentityProvider provider1 = new IdentityProvider();
        provider1.setId(1);
        provider1.setName("IP1");
        IdentityProviderChannel channel1 = new IdentityProviderChannel();
        IdentityVault identityVault1 = new IdentityVault();
        identityVault1.setId(1);
        identityVault1.setName("Vault1");
        channel1.setIdentityVault(identityVault1);
        provider1.setDefaultChannel(channel1);
        providers1.add(provider1);
        iau1.setProviders(providers1);
    	idaus1.add(iau1);
    	IdentityApplianceUnit iau2 = new IdentityApplianceUnit();
    	iau2.setName("Unit 2");
    	iau2.setType(IdentityApplianceUnitType.PROVISIONING_UNIT);
    	idaus1.add(iau2);
    	iaDeployment1.setIdaus(idaus1);
    	identityAppliance1.setIdApplianceDeployment(iaDeployment1);
    	identityAppliances.add(identityAppliance1);
    	
    	IdentityAppliance identityAppliance2 = new IdentityAppliance();
    	identityAppliance2.setId(2);
    	IdentityApplianceDefinition iad2 = new IdentityApplianceDefinition();
    	iad2.setName("Started appliance 2");
    	iad2.setDescription("Description 2");
    	Location location2 = new Location();
    	location2.setProtocol("https");
    	location2.setHost("host2.com");
    	location2.setPort(8090);
    	location2.setContext("context2");
    	location2.setUri("uri2");
    	iad2.setLocation(location2);
    	identityAppliance2.setIdApplianceDefinition(iad2);
    	IdentityApplianceDeployment iaDeployment2 = new IdentityApplianceDeployment();
    	iaDeployment2.setState("Started");
    	Set<IdentityApplianceUnit> idaus2 = new LinkedHashSet<IdentityApplianceUnit>();
    	IdentityApplianceUnit iau3 = new IdentityApplianceUnit();
    	iau3.setName("Unit 3");
    	iau3.setType(IdentityApplianceUnitType.FEDERATION_UNIT);
    	idaus2.add(iau3);
    	iaDeployment2.setIdaus(idaus2);
    	identityAppliance2.setIdApplianceDeployment(iaDeployment2);
    	identityAppliances.add(identityAppliance2);
    	
    	IdentityAppliance identityAppliance3 = new IdentityAppliance();
    	identityAppliance3.setId(3);
    	IdentityApplianceDefinition iad3 = new IdentityApplianceDefinition();
    	iad3.setName("Identity Appliance 3");
    	iad3.setDescription("Description 3");
    	Location location3 = new Location();
    	location3.setProtocol("http");
    	location3.setHost("host3.com");
    	location3.setPort(80);
    	location3.setContext("context3");
    	location3.setUri("uri3");
    	iad3.setLocation(location3);
    	identityAppliance3.setIdApplianceDefinition(iad3);
    	IdentityApplianceDeployment iaDeployment3 = new IdentityApplianceDeployment();
    	iaDeployment3.setState("Stopped");
    	Set<IdentityApplianceUnit> idaus3 = new LinkedHashSet<IdentityApplianceUnit>();
    	IdentityApplianceUnit iau4 = new IdentityApplianceUnit();
    	iau4.setName("Unit 4");
    	iau4.setType(IdentityApplianceUnitType.PROVISIONING_UNIT);
    	idaus3.add(iau4);
    	iaDeployment3.setIdaus(idaus3);
    	identityAppliance3.setIdApplianceDeployment(iaDeployment3);
    	identityAppliances.add(identityAppliance3);
	}

    public BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest request) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Supported!");
    }

    /**
     * Deploys an already existing Identity Appliance.  
     * The appliance was previously created or imported and can by found in the list of appliances.
     */
    public DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException {
    	DeployIdentityApplianceResponse response = new DeployIdentityApplianceResponse();
    	response.setDeployed(true);
    	return response;
    }

    /**
     * Undeploys an Identity Appliance.
     * The appliance was previously deployed, if the appliance is running this will first attempt to stop it.
     */
    public UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    //    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest request) {
//        if (request.isStartedOnly()) {
//        	ListIdentityAppliancesResponse response = new ListIdentityAppliancesResponse();
//        	response.getIdentityAppliances().addAll(getStartedIdentityAppliances());
//        	return response;
//        } else {
//        	ListIdentityAppliancesResponse response = new ListIdentityAppliancesResponse();
//        	response.getIdentityAppliances().addAll(identityAppliances);
//        	return response;
//        }
//    }
//
//    public FindIdentityApplianceByIdResponse findIdentityAppliancesById(FindIdentityApplianceByIdRequest request) {
//    	FindIdentityApplianceByIdResponse response = new FindIdentityApplianceByIdResponseImpl();
//    	IdentityAppliance identityAppliance = getIdentityAppliance(request.getId());
//    	response.setIdentityAppliance(identityAppliance);
//    	return response;
//    }
//
//    public FindIdentityAppliancesByStateResponse findIdentityAppliancesByState(FindIdentityAppliancesByStateRequest request) {
//        throw new UnsupportedOperationException("Not Implemented!");
//    }

    public ImportIdentityApplianceResponse importIdentityAppliance(ImportIdentityApplianceRequest request) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public ExportIdentityApplianceResponse exportIdentityAppliance(ExportIdentityApplianceRequest request) {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public ExportApplianceDefinitionResponse exportApplianceDefinition(ExportApplianceDefinitionRequest request) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public ImportApplianceDefinitionResponse importApplianceDefinition(ImportApplianceDefinitionRequest request) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Implemented!");
    }

    public ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) {
        ManageIdentityApplianceLifeCycleResponse response = new ManageIdentityApplianceLifeCycleResponse();
    	response.setAction(req.getAction());
        response.setStatusCode(StatusCode.STS_OK);
    	return response;
    }
    
    public AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        AddIdentityApplianceResponse res = null;
        try {

            logger.debug("Persisting identity appliance with name: " + req.getIdentityAppliance().getIdApplianceDefinition().getName());

            tx.begin();

            pm.makePersistent(req.getIdentityAppliance());

            tx.commit();

	    } catch (Exception e){
	        logger.error("Error persisting identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();

                throw new IdentityServerException("Transaction is still active. Performing rollback !!! ");
            }

	        // TODO : Do we have to close this ? pm.close();
            if(!pm.isClosed()){
                pm.close();
            }
	    }


		return res;
    }

    public UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Supported!");
    }

    public LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException {
        LookupIdentityApplianceByIdResponse res = new LookupIdentityApplianceByIdResponse();
        res.setIdentityAppliance(getIdentityAppliance(Long.parseLong(request.getIdentityApplianceId())));
		return res;
    }

    public RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException {
        if (req.isStartedOnly()) {
        	ListIdentityAppliancesResponse response = new ListIdentityAppliancesResponse();
        	response.getIdentityAppliances().addAll(getStartedIdentityAppliances());
        	return response;
        } else {
        	ListIdentityAppliancesResponse response = new ListIdentityAppliancesResponse();
        	response.getIdentityAppliances().addAll(identityAppliances);
        	return response;
        }
    }

    public ListIdentityAppliancesByStateResponse listIdentityAppliancesByState(ListIdentityAppliancesByStateRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public AddIdentityApplianceDefinitionResponse addIdentityApplianceDefinition(AddIdentityApplianceDefinitionRequest req) throws IdentityServerException {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        AddIdentityApplianceDefinitionResponse res = null;
        try {

            /* TODO : Improve
            if (!URLValidator.validateUrl(req.getLocation())){
                String msg = "URL Location is invalid :"+req.getLocation();
                logger.error(msg);
                throw new IdentityServerException("URL Location invalid :" + req.getLocation());
            }
            */


            logger.debug("Persisting identity appliance definition with name: " + req.getIdentityApplianceDefinition().getName());

            tx.begin();
            //pm.getFetchPlan().addGroup("idbus_f_group");
//            IdentityApplianceDefinition idbus = new IdentityApplianceDefinition();
//
//            //ID value will be set by datanucleus using native value strategy
//			idbus.setName(req.getName());
//            idbus.setDisplayName(req.getDisplayName());
//
//            // TODO : Fix-me idbus.setLocation(req.getLocation().getLocationAsString());
//            idbus.setActiveFeatures(req.getActiveFeatures());
//            idbus.setSupportedRoles(req.getSupportedProviderRoles());

            //TODO : Check if the idApplianceDefinition (and entire tree) is correct

            pm.makePersistent(req.getIdentityApplianceDefinition());

            tx.commit();

	    } catch (Exception e){
	        logger.error("Error persisting identity appliance definition", e);
	        throw new IdentityServerException(e);

	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();

                throw new IdentityServerException("Transaction is still active. Performing rollback !!! ");
//                res = new AddIdentityApplianceDefinitionResponse();
//                res.setStatusCode(StatusCode.STS_ERROR);
//                res.setErrorMsg("Transaction is still active. Performing rollback !!!");
            }

	        // TODO : Do we have to close this ? pm.close();
	    }


		return res;

    }

    public LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest request) throws IdentityServerException {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        LookupIdentityApplianceDefinitionByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(3); //fetching providers and channels as well
            logger.debug("Finding identity appliance definition by ID : "+ request.getIdentityApplianceDefinitionId());

            IdentityApplianceDefinition iad = pm.getObjectById(IdentityApplianceDefinition.class, new Long(request.getIdentityApplianceDefinitionId()));
            res = new LookupIdentityApplianceDefinitionByIdResponse();
            res.setIdentityApplianceDefinition(pm.detachCopy(iad));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving identity appliance definition with id : " + request.getIdentityApplianceDefinitionId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();

//                res = new LookupIdentityApplianceDefinitionByIdResponse();
//                res.setStatusCode(StatusCode.STS_ERROR);
//                res.setErrorMsg("Transaction is still active. Performing rollback !!!");
            }

	        // TODO : Do we have to close this ? pm.close();
	    }


		return res;
    }

    public LookupIdentityApplianceDefinitionResponse lookupIdentityApplianceDefinition(LookupIdentityApplianceDefinitionRequest request) throws IdentityServerException {
                PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        LookupIdentityApplianceDefinitionResponse res = null;
        try {
            tx.begin();

            pm.getFetchPlan().setMaxFetchDepth(3); //fetching providers and channels as well
            logger.debug("Finding identity appliance definition by id : "+ request.getId());

            Query query = pm.newQuery(IdentityApplianceDefinition.class, "id==:id");
            Collection result = (Collection) query.execute(request.getId());
            res = new LookupIdentityApplianceDefinitionResponse();


            if (result.isEmpty()){
                throw new IdentityServerException("Identity Appliance Definition with id: " +  request.getId() + "  not found");
            }
            Iterator iter = result.iterator();
            IdentityApplianceDefinition iad = (IdentityApplianceDefinition)iter.next();
            res.setIdentityAppliance(pm.detachCopy(iad));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving Identity Appliance Definitions", e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();

//                res = new LookupIdentityApplianceDefinitionResponse();
//                res.setStatusCode(StatusCode.STS_ERROR);
//                res.setErrorMsg("Transaction is still active. Performing rollback !!!");
            }

	        // TODO : Do we have to close this ? pm.close();
	    }


		return res;
    }


    public ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException {
//		return samlr2CapabilityMgr.listIdentityBuses(req);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        ListIdentityApplianceDefinitionsResponse res = new ListIdentityApplianceDefinitionsResponse();
        try {
            tx.begin();
            logger.debug("Listing all identity appliance definitions");
            pm.getFetchPlan().setMaxFetchDepth(3); //fetching providers and channels as well
            Extent e = pm.getExtent(IdentityApplianceDefinition.class,false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getIdentityApplianceDefinitions().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }

        return res;
    }

    /*************************************************
     * Empty implementations
     ************************************************/

    public ListIdentityVaultsResponse listIdentityVaults(ListIdentityVaultsRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListUserInformationLookupsResponse listUserInformationLookups(ListUserInformationLookupsRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListAccountLinkagePoliciesResponse listAccountLinkagePolicies(ListAccountLinkagePoliciesRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListAuthenticationContractsResponse listAuthenticationContracts(ListAuthenticationContractsRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListAuthenticationMechanismsResponse listAuthenticationMechanisms(ListAuthenticationMechanismsRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListAttributeProfilesResponse listAttributeProfiles(ListAttributeProfilesRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public ListAuthAssertionEmissionPoliciesResponse listAuthAssertionEmissionPolicies(ListAuthAssertionEmissionPoliciesRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupIdentityVaultByIdResponse lookupIdentityVaultById(LookupIdentityVaultByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupUserInformationLookupByIdResponse lookupUserInformationLookupById(LookupUserInformationLookupByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupAccountLinkagePolicyByIdResponse lookupAccountLinkagePolicyById(LookupAccountLinkagePolicyByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupAuthenticationContractByIdResponse lookupAuthenticationContractById(LookupAuthenticationContractByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupAuthenticationMechanismByIdResponse lookupAuthenticationMechanismById(LookupAuthenticationMechanismByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupAttributeProfileByIdResponse lookupAttributeProfileById(LookupAttributeProfileByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupAuthAssertionEmissionPolicyByIdResponse lookupAuthAssertionEmissionPolicyById(LookupAuthAssertionEmissionPolicyByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public AddResourceResponse addResource(AddResourceRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    public LookupResourceByIdResponse lookupResourceById(LookupResourceByIdRequest req) throws IdentityServerException {
        //TODO implement
        return null;
    }

    private IdentityAppliance getIdentityAppliance(long id) {
    	for (IdentityAppliance ia : identityAppliances) {
    		if (ia.getId() == id) {
    			return ia;
    		}
    	}
    	return null;
    }
    
    private List<IdentityAppliance> getStartedIdentityAppliances() {
    	List<IdentityAppliance> startedIdentityAppliances = new ArrayList<IdentityAppliance>();
    	for (IdentityAppliance ia : identityAppliances) {
    		if (ia.getId() == 1 || ia.getId() == 2) {
    			startedIdentityAppliances.add(ia);
    		}
    	}
    	return startedIdentityAppliances;
    }
}
