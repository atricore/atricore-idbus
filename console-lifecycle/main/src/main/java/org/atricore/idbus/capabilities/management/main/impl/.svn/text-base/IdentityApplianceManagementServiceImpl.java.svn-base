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

package org.atricore.idbus.capabilities.management.main.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceState;
import org.atricore.idbus.capabilities.management.main.domain.metadata.*;
import org.atricore.idbus.capabilities.management.main.exception.IdentityApplianceMetadataManagementException;
import org.atricore.idbus.capabilities.management.main.exception.IdentityServerException;
import org.atricore.idbus.capabilities.management.main.spi.*;
import org.atricore.idbus.capabilities.management.main.spi.request.*;
import org.atricore.idbus.capabilities.management.main.spi.response.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ByteArrayResource;

import javax.jdo.*;

import java.util.*;

public class IdentityApplianceManagementServiceImpl implements
        IdentityApplianceManagementService, DisposableBean, InitializingBean {

	private static final Log logger = LogFactory.getLog(IdentityApplianceManagementServiceImpl.class);

    private IdentityApplianceBuilder builder;

    private IdentityApplianceRegistry registry;

    private IdentityApplianceDeployer deployer;

    private PersistenceManagerFactory pmf;

    private PersistenceManager pm;

//    private DozerBeanMapper dozerMapper;

    public void destroy() throws Exception {
        try {
            pm.close();
        } catch (Exception e) {
            logger.error("Closing Persistence Manager : " + e.getMessage(), e);
        }
    }

    public void afterPropertiesSet() throws Exception {
        pm = pmf.getPersistenceManager();
    }

    public BuildIdentityApplianceResponse buildIdentityAppliance(BuildIdentityApplianceRequest request) throws IdentityServerException {

        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(FetchPlan.FETCH_SIZE_GREEDY);
            IdentityAppliance appliance = lookupById(Long.parseLong(request.getApplianceId()));
            appliance = buildAppliance(appliance, request.isDeploy());
            tx.commit();
            return new BuildIdentityApplianceResponse(appliance);
	    } catch (Exception e){
	        logger.error("Error building identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    /**
     * Deploys an already existing Identity Appliance.  
     * The appliance was previously created or imported and can by found in the list of appliances.
     */
    public DeployIdentityApplianceResponse deployIdentityAppliance(DeployIdentityApplianceRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            IdentityAppliance appliance = lookupById(Long.parseLong(req.getApplianceId()));
            appliance = deployAppliance(appliance);
            if (req.getStartAppliance()) {
                appliance = startAppliance(appliance);
            }
            tx.commit();
            return new DeployIdentityApplianceResponse(appliance, true);
	    } catch (Exception e){
	        logger.error("Error deploying identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    /**
     * Undeploys an Identity Appliance.
     * The appliance was previously deployed, if the appliance is running this will first attempt to stop it.
     */
    public UndeployIdentityApplianceResponse undeployIdentityAppliance(UndeployIdentityApplianceRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        try {
            IdentityAppliance appliance = lookupById(Long.parseLong(req.getApplianceId()));
            appliance = undeployAppliance(appliance);
            return new UndeployIdentityApplianceResponse (appliance);
	    } catch (Exception e){
	        logger.error("Error undeploying identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    public StartIdentityApplianceResponse startIdentityAppliance(StartIdentityApplianceRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        try {
            IdentityAppliance appliance = lookupById(Long.parseLong(req.getId()));
            appliance = startAppliance(appliance);
            return new StartIdentityApplianceResponse (appliance);
	    } catch (Exception e){
	        logger.error("Error starting identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    public StopIdentityApplianceResponse stopIdentityAppliance(StopIdentityApplianceRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        try {
            IdentityAppliance appliance = lookupById(Long.parseLong(req.getId()));
            appliance = stopAppliance(appliance);
            return new StopIdentityApplianceResponse (appliance);
	    } catch (Exception e){
	        logger.error("Error stopping identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    public ImportIdentityApplianceResponse importIdentityAppliance(ImportIdentityApplianceRequest request) {
        throw new UnsupportedOperationException("Not Supported!");
    }

    public ExportIdentityApplianceResponse exportIdentityAppliance(ExportIdentityApplianceRequest request) {
        throw new UnsupportedOperationException("Not Supported!");
    }

    public ImportApplianceDefinitionResponse importApplianceDefinition(ImportApplianceDefinitionRequest request) throws IdentityServerException {

        Transaction tx = pm.currentTransaction();
        try {

            if (logger.isTraceEnabled())
                logger.trace("Importing appliance definition \n" + request.getDescriptor() + "\n");

            // 1. Instantiate beans
            GenericApplicationContext ctx = new GenericApplicationContext();
            ctx.setClassLoader(getClass().getClassLoader());

            XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
            xmlReader.loadBeanDefinitions(new ByteArrayResource(request.getDescriptor().getBytes()));
            ctx.refresh();

            Map<String, IdentityApplianceDefinition> definitions = ctx.getBeansOfType(IdentityApplianceDefinition.class);
            if (definitions.size() < 1 )
                throw new IdentityServerException("No Identity Appliance Definition found in the given descriptor!");
            if (definitions.size() > 1)
                throw new IdentityServerException("Only one Identity Appliance definition is supported per descriptor. (found "+definitions.size()+")");

            IdentityApplianceDefinition applianceDef = definitions.values().iterator().next();
            if (logger.isDebugEnabled())
                    logger.debug("Received Identity Appliance Definition : [" +
                            applianceDef.getId() + "] " +
                    applianceDef.getName() + ":" +
                    applianceDef.getDescription());

            applianceDef.setRevision(1);
            applianceDef.setLastModification(new Date());

            // 2. Create Identity Appliance

            if (logger.isTraceEnabled())
                logger.trace("Creating Identity Appliance");

            IdentityAppliance appliance = new IdentityAppliance ();

            appliance.setIdApplianceDefinition(applianceDef);
            appliance.setState(IdentityApplianceState.PROJECTED.toString());

            appliance = addAppliance(appliance);

            if (logger.isTraceEnabled())
                logger.trace("Created Identity Appliance " + appliance.getId());

            // 4. Return the appliance
            ImportApplianceDefinitionResponse response = new ImportApplianceDefinitionResponse();
            response.setAppliance(appliance);

            return response;

        } catch (Exception e) {
	        logger.error("Error importing identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    public ManageIdentityApplianceLifeCycleResponse manageIdentityApplianceLifeCycle(ManageIdentityApplianceLifeCycleRequest req) throws IdentityServerException {

        Transaction tx = pm.currentTransaction();
        try {
            String appId = req.getApplianceId();
            Long id = Long.parseLong(appId);

            pm.getFetchPlan().setMaxFetchDepth(FetchPlan.FETCH_SIZE_GREEDY);
            IdentityAppliance appliance = lookupById(id);

            tx.begin();

            switch (req.getAction()) {
                case START:
                    startAppliance(appliance);
                    break;
                case STOP:
                    stopAppliance(appliance);
                    break;
                case RESTART:
                    restartAppliance(appliance);
                    break;
                case UNINSTALL:
                    undeployAppliance(appliance);
                    break;
                default:
                    throw new UnsupportedOperationException("Appliance lifecycle management action not supported: " + req.getAction());
            }

            ManageIdentityApplianceLifeCycleResponse response = new ManageIdentityApplianceLifeCycleResponse(req.getAction(), appliance);
            response.setStatusCode(StatusCode.STS_OK);
            tx.commit();
            return response;
        } catch (Exception e){
	        logger.error("Error processing identity appliance lifecycle action", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }

    }

    public AddIdentityApplianceResponse addIdentityAppliance(AddIdentityApplianceRequest req) throws IdentityServerException {
        AddIdentityApplianceResponse res = null;
        Transaction tx = pm.currentTransaction();
        try {
            IdentityAppliance appliance = this.addAppliance(req.getIdentityAppliance());

            if (appliance.getIdApplianceDefinition() == null)
                throw new IdentityServerException("Appliances must contain an appliance definition!");

            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();
            applianceDef.setRevision(1);
            applianceDef.setLastModification(new Date());
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(6);
            appliance = pm.detachCopy(addAppliance(appliance));

            res = new AddIdentityApplianceResponse();
            res.setAppliance(appliance);

            tx.commit();

        } catch (Exception e){
	        logger.error("Error adding identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
        return res;
    }

    public UpdateIdentityApplianceResponse updateIdentityAppliance(UpdateIdentityApplianceRequest request) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        UpdateIdentityApplianceResponse res = null;
        try {
            IdentityAppliance appliance = null;
//            tx.begin();
//            appliance = pm.detachCopy(pm.getObjectById(IdentityAppliance.class, request.getAppliance().getId()));
//            tx.commit();
            appliance = request.getAppliance();
            IdentityApplianceDefinition applianceDef = appliance.getIdApplianceDefinition();

            applianceDef.setLastModification(new Date());
            applianceDef.setRevision(applianceDef.getRevision() + 1);

            tx.begin();
            this.updateAppliance(appliance);
            tx.commit();
            res = new UpdateIdentityApplianceResponse(appliance);
        } catch (Exception e){
	        logger.error("Error updating identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
        return res;
    }

    public LookupIdentityApplianceByIdResponse lookupIdentityApplianceById(LookupIdentityApplianceByIdRequest request) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupIdentityApplianceByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(6);
            IdentityAppliance appliance = lookupById(Long.parseLong(request.getIdentityApplianceId()));
            res = new LookupIdentityApplianceByIdResponse();
            res.setIdentityAppliance(appliance);

            tx.commit();

        } catch (Exception e){
	        logger.error("Error looking for identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
        return res;
    }

    public RemoveIdentityApplianceResponse removeIdentityAppliance(RemoveIdentityApplianceRequest req) throws IdentityServerException{
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            //First remove deployment data to prevent reference error when deleting providers
            req.getIdentityAppliance().setIdApplianceDeployment(null);
            this.updateAppliance(req.getIdentityAppliance());
            
            //Next, remove providers (and channels with them) to prevent reference error when performing cascade-delete on vaults (while deleting appliance)
            req.getIdentityAppliance().getIdApplianceDefinition().setProviders(null);
            this.updateAppliance(req.getIdentityAppliance());

            //After that remove the appliance
            this.remove(req.getIdentityAppliance());
            RemoveIdentityApplianceResponse res = new RemoveIdentityApplianceResponse();
            tx.commit();
            return res;
        } catch (Exception e){
	        logger.error("Error removing identity appliance", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
    }

    public ListIdentityAppliancesResponse listIdentityAppliances(ListIdentityAppliancesRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListIdentityAppliancesResponse res = null;
        try {
            tx.begin();
            Collection<IdentityAppliance> appliances = this.list(req.isStartedOnly(), 6);

            res = new ListIdentityAppliancesResponse ();
            res.setIdentityAppliances(appliances);

            tx.commit();
        } catch (Exception e){
	        logger.error("Error listing identity appliances", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
        return res;
    }

    public ListIdentityAppliancesByStateResponse listIdentityAppliancesByState(ListIdentityAppliancesByStateRequest req) throws IdentityServerException {
        throw new UnsupportedOperationException("Not Supported!");
    }

    public AddIdentityApplianceDefinitionResponse addIdentityApplianceDefinition(AddIdentityApplianceDefinitionRequest req)
            throws IdentityServerException {

        Transaction tx = pm.currentTransaction();
        AddIdentityApplianceDefinitionResponse res = null;
        try {

            /* TODO : Improve
            if (!URLValidator.validateUrl(req.getLocation())){
                String msg = "URL Location is invalid :"+req.getLocation();
                logger.error(msg);
                throw new IdentityApplianceMetadataManagementException("URL Location invalid :" + req.getLocation());
            }
            */

            logger.debug("Persisting identity appliance definition with name: " + req.getIdentityApplianceDefinition().getName());

            tx.begin();

            //TODO : Check if the idApplianceDefinition (and entire tree) is correct
            pm.makePersistent(req.getIdentityApplianceDefinition());

            tx.commit();

	    } catch (Exception e){
	        logger.error("Error adding identity appliance definition", e);
	        throw new IdentityServerException(e);

	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupIdentityApplianceDefinitionByIdResponse lookupIdentityApplianceDefinitionById(LookupIdentityApplianceDefinitionByIdRequest request) throws IdentityServerException {

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
            }

	    }
		return res;
    }

    public LookupIdentityApplianceDefinitionResponse lookupIdentityApplianceDefinition(LookupIdentityApplianceDefinitionRequest request) throws IdentityServerException {

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
                throw new IdentityApplianceMetadataManagementException("Identity Appliance Definition with id: " +  request.getId() + "  not found");
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
            }

	    }


		return res;
    }

    public ListIdentityApplianceDefinitionsResponse listIdentityApplianceDefinitions(ListIdentityApplianceDefinitionsRequest req) throws IdentityServerException {

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

        }
        return res;
    }

    /***************************************************************
     * List methods
     ***************************************************************/

    public ListIdentityVaultsResponse listIdentityVaults(ListIdentityVaultsRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListIdentityVaultsResponse res = new ListIdentityVaultsResponse();
        try {
            tx.begin();
            logger.debug("Listing all identity vaults");
            pm.getFetchPlan().setMaxFetchDepth(2); //fetching user lookup information as well
            Extent e = pm.getExtent(IdentityVault.class, true);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getIdentityVaults().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    public ListUserInformationLookupsResponse listUserInformationLookups(ListUserInformationLookupsRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListUserInformationLookupsResponse res = new ListUserInformationLookupsResponse();
        try {
            tx.begin();
            logger.debug("Listing all user information lookups");
            pm.getFetchPlan().setMaxFetchDepth(1); 
            Extent e = pm.getExtent(UserInformationLookup.class, false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getUserInfoLookups().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    public ListAccountLinkagePoliciesResponse listAccountLinkagePolicies(ListAccountLinkagePoliciesRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListAccountLinkagePoliciesResponse res = new ListAccountLinkagePoliciesResponse();
        try {
            tx.begin();
            logger.debug("Listing all account linkage policies");
            pm.getFetchPlan().setMaxFetchDepth(1);
            Extent e = pm.getExtent(AccountLinkagePolicy.class, false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getAccountLinkagePolicies().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    public ListAuthenticationContractsResponse listAuthenticationContracts(ListAuthenticationContractsRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListAuthenticationContractsResponse res = new ListAuthenticationContractsResponse();
        try {
            tx.begin();
            logger.debug("Listing all authentication contracts");
            pm.getFetchPlan().setMaxFetchDepth(1);
            Extent e = pm.getExtent(AuthenticationContract.class, false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getAuthContracts().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    public ListAuthenticationMechanismsResponse listAuthenticationMechanisms(ListAuthenticationMechanismsRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListAuthenticationMechanismsResponse res = new ListAuthenticationMechanismsResponse();
        try {
            tx.begin();
            logger.debug("Listing all authentication mechanisms");
            pm.getFetchPlan().setMaxFetchDepth(1);
            Extent e = pm.getExtent(AuthenticationMechanism.class, false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getAuthMechanisms().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    public ListAttributeProfilesResponse listAttributeProfiles(ListAttributeProfilesRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListAttributeProfilesResponse res = new ListAttributeProfilesResponse();
        try {
            tx.begin();
            logger.debug("Listing all attribute profiles");
            pm.getFetchPlan().setMaxFetchDepth(1);
            Extent e = pm.getExtent(AttributeProfile.class, false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getAttributeProfiles().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    public ListAuthAssertionEmissionPoliciesResponse listAuthAssertionEmissionPolicies(ListAuthAssertionEmissionPoliciesRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        ListAuthAssertionEmissionPoliciesResponse res = new ListAuthAssertionEmissionPoliciesResponse();
        try {
            tx.begin();
            logger.debug("Listing all authentication assertion emission policies");
            pm.getFetchPlan().setMaxFetchDepth(1);
            Extent e = pm.getExtent(AuthenticationAssertionEmissionPolicy.class, false);
            Query  q = pm.newQuery(e);
            Collection result = (Collection)q.execute();

            if(!result.isEmpty()){
                res.getAuthEmissionPolicies().addAll(pm.detachCopyAll(result));
            }
            tx.commit();
        } finally {

            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return res;
    }

    /***************************************************************
     * Lookup methods
     ***************************************************************/

    public LookupIdentityVaultByIdResponse lookupIdentityVaultById(LookupIdentityVaultByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupIdentityVaultByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(2);
            logger.debug("Finding identity vault by ID : "+ req.getIdentityVaultId());

            IdentityVault identityVault = pm.getObjectById(IdentityVault.class, new Long(req.getIdentityVaultId()));
            res = new LookupIdentityVaultByIdResponse();
            res.setIdentityVault(pm.detachCopy(identityVault));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving identity vault with id : " + req.getIdentityVaultId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupUserInformationLookupByIdResponse lookupUserInformationLookupById(LookupUserInformationLookupByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupUserInformationLookupByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(1);
            logger.debug("Finding user information lookup by ID : "+ req.getUserInformationLookupId());

            UserInformationLookup userInformationLookup = pm.getObjectById(UserInformationLookup.class, new Long(req.getUserInformationLookupId()));
            res = new LookupUserInformationLookupByIdResponse();
            res.setUserInfoLookup(pm.detachCopy(userInformationLookup));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving user information lookup with id : " + req.getUserInformationLookupId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupAccountLinkagePolicyByIdResponse lookupAccountLinkagePolicyById(LookupAccountLinkagePolicyByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupAccountLinkagePolicyByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(1);
            logger.debug("Finding account linkage policy by ID : "+ req.getAccountLinkagePolicyId());

            AccountLinkagePolicy policy = pm.getObjectById(AccountLinkagePolicy.class, new Long(req.getAccountLinkagePolicyId()));
            res = new LookupAccountLinkagePolicyByIdResponse();
            res.setAccountLinkagePolicy(pm.detachCopy(policy));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving account linkage policy with id : " + req.getAccountLinkagePolicyId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupAuthenticationContractByIdResponse lookupAuthenticationContractById(LookupAuthenticationContractByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupAuthenticationContractByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(1);
            logger.debug("Finding authentication contract by ID : "+ req.getAuthenticationContactId());

            AuthenticationContract authenticationContract = pm.getObjectById(AuthenticationContract.class, new Long(req.getAuthenticationContactId()));
            res = new LookupAuthenticationContractByIdResponse();
            res.setAuthenticationContract(pm.detachCopy(authenticationContract));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving authentication contract with id : " + req.getAuthenticationContactId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupAuthenticationMechanismByIdResponse lookupAuthenticationMechanismById(LookupAuthenticationMechanismByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupAuthenticationMechanismByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(1);
            logger.debug("Finding authentication mechanism by ID : "+ req.getAuthMechanismId());

            AuthenticationMechanism authenticationMechanism = pm.getObjectById(AuthenticationMechanism.class, new Long(req.getAuthMechanismId()));
            res = new LookupAuthenticationMechanismByIdResponse();
            res.setAuthenticationMechanism(pm.detachCopy(authenticationMechanism));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving authentication mechanism with id : " + req.getAuthMechanismId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupAttributeProfileByIdResponse lookupAttributeProfileById(LookupAttributeProfileByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupAttributeProfileByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(1);
            logger.debug("Finding attribute profile by ID : "+ req.getAttributeProfileId());

            AttributeProfile attributeProfile = pm.getObjectById(AttributeProfile.class, new Long(req.getAttributeProfileId()));
            res = new LookupAttributeProfileByIdResponse();
            res.setAttributeProfile(pm.detachCopy(attributeProfile));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving attribute profile with id : " + req.getAttributeProfileId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupAuthAssertionEmissionPolicyByIdResponse lookupAuthAssertionEmissionPolicyById(LookupAuthAssertionEmissionPolicyByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupAuthAssertionEmissionPolicyByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(1);
            logger.debug("Finding authentication assertion emission policy by ID : "+ req.getAuthAssertionEmissionPolicyId());

            AuthenticationAssertionEmissionPolicy policy = pm.getObjectById(AuthenticationAssertionEmissionPolicy.class, new Long(req.getAuthAssertionEmissionPolicyId()));
            res = new LookupAuthAssertionEmissionPolicyByIdResponse();
            res.setPolicy(pm.detachCopy(policy));

            tx.commit();
	    } catch (Exception e){
	        logger.error("Error retrieving authentication assertion emission policy with id : " + req.getAuthAssertionEmissionPolicyId(), e);
	        throw new IdentityServerException(e);
	    } finally {

	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public AddResourceResponse addResource(AddResourceRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        AddResourceResponse res = null;
        try {

            logger.debug("Persisting resource with name: " + req.getResource().getName());

            tx.begin();
            
            Resource resource = pm.makePersistent(req.getResource());

            res = new AddResourceResponse();
            res.setResource(resource);
            
            tx.commit();

	    } catch (Exception e){
	        logger.error("Error adding resource", e);
	        throw new IdentityServerException(e);

	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }

	    }
		return res;
    }

    public LookupResourceByIdResponse lookupResourceById(LookupResourceByIdRequest req) throws IdentityServerException {
        Transaction tx = pm.currentTransaction();
        LookupResourceByIdResponse res = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(FetchPlan.FETCH_SIZE_GREEDY);

            Long id = Long.parseLong(req.getResourceId());
            Resource resource = pm.getObjectById(Resource.class, id);
            resource = pm.detachCopy(resource);

            res = new LookupResourceByIdResponse();
            res.setResource(resource);

            tx.commit();
        } catch (Exception e){
	        logger.error("Error looking for resource", e);
	        throw new IdentityServerException(e);
	    } finally {
	        if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active. Performing rollback !!! ");
                tx.rollback();
            }
	    }
        return res;
    }

// -------------------------------------------------< Properties >

    public void setPmf(PersistenceManagerFactory pmf) {
        this.pmf = pmf;
    }

    public IdentityApplianceBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(IdentityApplianceBuilder builder) {
        this.builder = builder;
    }

    public IdentityApplianceRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(IdentityApplianceRegistry registry) {
        this.registry = registry;
    }

    public IdentityApplianceDeployer getDeployer() {
        return deployer;
    }

    public void setDeployer(IdentityApplianceDeployer deployer) {
        this.deployer = deployer;
    }


    // -------------------------------------------------< Protected Utils , they need transactional context !>

    // Maybe we can move this to a DAO?
    
    protected IdentityAppliance lookupById(long id) throws IdentityServerException {

        IdentityAppliance identityAppliance = pm.getObjectById(IdentityAppliance.class, id);
        identityAppliance = pm.detachCopy(identityAppliance);
        return identityAppliance;

    }
    
    protected Collection<IdentityAppliance> list(boolean deployedOnly, int fetchDepth) {

        logger.debug("Listing all identity appliances");
        pm.getFetchPlan().setMaxFetchDepth(fetchDepth);
        Collection result = null;

        if(deployedOnly){
            // TODO : Deployed
            Query query = pm.newQuery("SELECT FROM org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance" +
                    //" WHERE this.idApplianceDeployment != null");
                    " WHERE this.state == '" + IdentityApplianceState.STARTED + "'");
            result = (Collection)query.execute();
        } else {
            //TODO for now returning all appliances for list of projected
            Extent e = pm.getExtent(IdentityAppliance.class,false);
            Query  query = pm.newQuery(e);
            result = (Collection)query.execute();
        }


        if(!result.isEmpty()){
            return pm.detachCopyAll(result);
        } else {
            return result;
        }
            
    }
    
    protected IdentityAppliance addAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Adding Identity Appliance " + appliance.getId());

        return pm.makePersistent(appliance);
    }

    protected IdentityAppliance updateAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Updating Identity Appliance " + appliance.getId());

        return pm.makePersistent(appliance);
    }

    protected IdentityAppliance startAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Starting Identity Appliance " + appliance.getId());

        if (appliance.getState().equals(IdentityApplianceState.PROJECTED.toString()))
            appliance = buildAppliance(appliance, true);

        appliance = deployer.start(appliance);
        appliance = updateAppliance(appliance);

        return appliance;
    }

    protected IdentityAppliance stopAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Stopping Identity Appliance " + appliance.getId());

        appliance = deployer.stop(appliance);
        appliance = updateAppliance(appliance);

        return appliance;
    }

    protected IdentityAppliance restartAppliance(IdentityAppliance appliance) throws IdentityServerException {

        if (logger.isDebugEnabled())
            logger.debug("Restarting Identity Appliance " + appliance.getId());

        appliance = stopAppliance(appliance);
        appliance = startAppliance(appliance);

        return appliance;
    }

    protected IdentityAppliance undeployAppliance(IdentityAppliance appliance) throws IdentityServerException {
        if (logger.isDebugEnabled())
            logger.debug("Undeploying Identity Appliance " + appliance.getId());


        if (appliance.getState().equals(IdentityApplianceState.STARTED.toString()))
            appliance = stopAppliance(appliance);

        // Install it
        appliance = deployer.undeploy(appliance);

        // Store it
        appliance = this.updateAppliance(appliance);
        return appliance;

    }

    protected void remove(IdentityAppliance appliance) throws IdentityServerException {

//        Transaction tx=pm.currentTransaction();
        try {
        	logger.debug("Deleting identity appliance with id: " + appliance.getId());
//            tx.begin();
            IdentityAppliance identityAppliance = pm.getObjectById(IdentityAppliance.class, appliance.getId());

            pm.deletePersistent(identityAppliance);

//            tx.commit();

        } catch (Exception e){
            logger.error("Error removing a Identity Appliance",e);
            throw new IdentityServerException(e);

        } finally {
//            if (tx.isActive()) {
//                tx.rollback();
//            }
//            pm.close();
        }
    }

    protected IdentityAppliance deployAppliance(IdentityAppliance appliance) throws IdentityServerException {

        if (logger.isDebugEnabled())
            logger.debug("Deploying Identity Appliance " + appliance.getId());

        if (appliance.getState().equals(IdentityApplianceState.STARTED.toString()))
            appliance = stopAppliance(appliance);

        if (appliance.getState().equals(IdentityApplianceState.INSTALLED.toString()))
            appliance = undeployAppliance(appliance);

        if (appliance.getIdApplianceDeployment() == null)
            appliance = buildAppliance(appliance, false);

        // Install it
        appliance = deployer.deploy(appliance);

        // Store it
        appliance = this.updateAppliance(appliance);
        return appliance;
    }


    protected IdentityAppliance buildAppliance(IdentityAppliance appliance, boolean deploy) throws IdentityServerException {

        if (logger.isDebugEnabled())
            logger.debug("Building Identity Appliance [deploy:"+deploy+"]" + appliance.getId());

        // quick fix (sort providers: identity providers -> binding provider -> service providers -> binding provider -> service providers, ...)
        List<Provider> providers = appliance.getIdApplianceDefinition().getProviders();
        List<Provider> sortedProviders = new ArrayList<Provider>();
        for (Provider provider : providers) {
            if (provider instanceof ServiceProvider) {
                sortedProviders.add(provider);
            }
        }
        Collections.sort(sortedProviders, new ServiceProviderComparator());
        for (Provider provider : providers) {
            if (provider instanceof IdentityProvider) {
                sortedProviders.add(0, provider);
            } else if (provider instanceof BindingProvider) {
                int i = 0;
                boolean added = false;
                for (Provider sortedProvider : sortedProviders) {
                    if (sortedProvider instanceof ServiceProvider &&
                            ((ServiceProvider) sortedProvider).getBindingChannel().getTarget().equals(provider)) {
                        sortedProviders.add(i, provider);
                        added = true;
                        break;
                    }
                    i++;
                }
                if (!added) {
                    sortedProviders.add(provider);
                }
            }
        }

        appliance.getIdApplianceDefinition().setProviders(sortedProviders);

        // Build the appliance
        appliance = builder.build(appliance);

        // Install it
        if (deploy)
            appliance = deployAppliance(appliance);

        // Store it
        appliance = updateAppliance(appliance);
        return appliance;
    }

    public class ServiceProviderComparator implements Comparator<Provider> {
        public int compare(Provider sp1, Provider sp2) {
            if (((ServiceProvider)sp1).getBindingChannel().getTarget().equals(((ServiceProvider)sp2).getBindingChannel().getTarget())) return 0;
            else return 1;
        }
    }
}
