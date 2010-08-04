/*
 * Atricore IDBus
 *
 *     Copyright 2009, Atricore Inc.
 *
 *     This is free software; you can redistribute it and/or modify it
 *     under the terms of the GNU Lesser General Public License as
 *     published by the Free Software Foundation; either version 2.1 of
 *     the License, or (at your option) any later version.
 *
 *     This software is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this software; if not, write to the Free
 *     Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *     02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.test;
/*
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
  */
public class IDBusMetadataManagementTest {

    /*

	static Log logger = LogFactory.getLog(IDBusMetadataManagementTest.class);

	private IdentityBusMetadataManager idbusMgr;
	private SpringMetadataManager smm;
	private BeanUtils beanUtils;

    private static String IDBUS_DISPLAY_NAME = "idbus1";
    private static final String SP_DISPLAY_NAME = "sp1";
    private static final String IDP_DISPLAY_NAME = "idp1";
	private static final String SP1_DISPLAY_CHANNEL_NAME = "sp1_channel1";
	private static final String IDP1_DISPLAY_CHANNEL_NAME = "idp1_channel1";
	private String idbusName;
	private String idpName;
	private String spName;
	private String spChannelName;
	private String idpChannelName;
	
	private ApplicationContext applicationContext;

    @Before
	public void setUp(){
    	applicationContext = new ClassPathXmlApplicationContext(
                 new String[]{"/org/atricore/idbus/applications/server/impl/service-integration-test.xml"}
         );
		idbusMgr = (IdentityBusMetadataManager)applicationContext.getBean("identityBusMetadataManager");
		smm = (SpringMetadataManager)applicationContext.getBean("smm");
		beanUtils = (BeanUtils)applicationContext.getBean("beanUtils");
	}
    
    @Test
    public void testIDBusMetadataManagement() throws Exception{
    	testAddIdentityBus();
    	testFindIdentityBus();
    	testUpdateIdentityBus();
    	testAddSPProvider();
    	testFindSPProvider();
    	testUpdateSPProvider();
    	testAddIDPProvider();
    	testFindIDPProvider();
    	testUpdateIDPProvider();
    	testAddSPChannel();
    	testFindSPChannel();
    	testUpdateSPChannel();
//    	testGenerateSPChannelMetadataDescriptor();
    	testAddIDPChannel();
    	testFindIDPChannel();
    	testUpdateIDPChannel();
    	testGenerateIDPChannelMetadataDescriptor();
    	testReadChildElements();
    	testGenerateSpringConfiguration();
    	testDeleteSPChannel();
    	testDeleteIDPChannel();
    	testDeleteSPProvider();
    	testDeleteIDPProvider();
    	findDeletedSPProvider();
    	findDeletedIDPProvider();
    	testDeleteIdentityBus();
    	findDeletedIDBus();
    }
	
	private void testGenerateSpringConfiguration() {
		try {
			idbusMgr.generateSpringConfiguration();
		} catch (IdentityApplianceMetadataManagementException e) {
			logger.equals(e);
			assert false : "Failed to generate spring configuration";
		}
	}

	//@Test
	public void testAddIdentityBus(){
		AddIdentityApplianceDefinitionRequest req = new AddIdentityBusRequestImpl();
        logger.debug("AddIdentityBus test");
        req.setDisplayName(IDBUS_DISPLAY_NAME);
		req.setDescription("idbus1 desc");
		req.setLocation("http://localhost:8080/idbus1");
		req.setIdentityProviderModeSupported(true);
		req.setServiceProviderModeSupported(false);
		AddIdentityApplianceDefinitionResponse res = null;
		try {
			res = idbusMgr.addIdentityApplianceDefinition(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "Failed to add identity bus";
			logger.error(e);
		}
		idbusName = res.getApplinaceDefinition().getName();
	}
	
	//@Test
	public void testFindIdentityBus(){
        logger.debug("FindIdentityBus test");
		FindIdentityBusByNameRequest req = new FindIdentityBusByNameRequestImpl();
		req.setName(idbusName);
		FindIdentityBusByNameResponse res = null;
		try {
			res = idbusMgr.findIdentityBusByName(req);
		} catch (IdentityApplianceMetadataElementNotFoundException e) {
			assert false : "Failed to find identity bus with name:" + req.getName();
			logger.error(e);
			return;
		}
		assert res.getApplinaceDefinition().getName().equals(req.getName()) : "Name not persisted correctly";
		assert res.getApplinaceDefinition().getDisplayName().equals(IDBUS_DISPLAY_NAME) : "Display name not persisted correctly";
		assert res.getApplinaceDefinition().getDescription().equals("idbus1 desc") : "Description not persisted correctly";
		assert res.getApplinaceDefinition().getLocation().equals("http://localhost:8080/idbus1") : "Location not persisted correctly";
		assert res.getApplinaceDefinition().isIdentityProviderModeSupported() : "identityProviderModeSupported not persisted correctly";
		assert res.getApplinaceDefinition().isServiceProviderModeSupported() == false : "serviceProviderModeSupported not persisted correctly";

		//TODO uncomment when bean is persisted (not persisted for now to keep it from 'spoiling' spring-config.xml
//		Bean bean = null;
//		
//		try {
//			bean = smm.findBeanByName("identity-bus" + idbusName);
//		} catch (SpringMetadataManagementException e) {
//			assert false : "Failed to find identity bus bean with name:" + req.getName();
//			logger.error(e);
//			return;
//		}
//		assert bean.getName().equals("identity-bus" + idbusName) : "Bean name not persited correctly";
	}

    //@Test
    public void testUpdateIdentityBus(){
        logger.debug("UpdateIdentityBus test");
        UpdateIdentityApplianceDefinitionRequest req = new UpdateIdentityBusRequestImpl();
        req.setName(idbusName);
        req.setDisplayName(IDBUS_DISPLAY_NAME + "_updated");
        String descUpdated = "idbus1 desc_updated";
        req.setDescription(descUpdated);
        String locationUpdated = "http://localhost:8080/idbus1_updated";
        req.setLocation(locationUpdated);
        req.setIdentityProviderModeSupported(false);
        req.setServiceProviderModeSupported(true);
        UpdateIdentityApplianceDefinitionResponse res = null;
        try {
            res = idbusMgr.updateIdentityBus(req);
        } catch (IdentityApplianceMetadataManagementException e) {
            assert false : "Failed to update identity bus with name:" + req.getName();
            logger.error(e);
            return;
        }
        assert res.getApplinaceDefinition().getDisplayName().equals(IDBUS_DISPLAY_NAME + "_updated") : "Display name not updated correctly";
        assert res.getApplinaceDefinition().getLocation().equals(locationUpdated) : "Location not updated correctly";
        assert res.getApplinaceDefinition().getDescription().equals(descUpdated) : "Description not updated correctly";
        assert res.getApplinaceDefinition().isIdentityProviderModeSupported() == false : "identityProviderModeSupported not updated correctly";
        assert res.getApplinaceDefinition().isServiceProviderModeSupported() == true : "serviceProviderModeSupported not updated correctly";
    }

    //@Test
    public void testAddSPProvider() throws Exception{
        logger.debug("AddSPProvider test");
        AddSAMLR2SPProviderRequest req = new AddServiceProviderRequest();
        req.setDisplayName(SP_DISPLAY_NAME);
        String spDesc = "sp1 desc";
        req.setDescription(spDesc);
        String spLocation = "http://localhost:8080/idbus1/sp1";
        req.setLocation(spLocation);
        req.setEncryptAuthenticationRequest(false);
        req.setSignAuthenticationRequest(false);
        
        FindIdentityBusByNameRequest idbusFindReq = new FindIdentityBusByNameRequestImpl();
        idbusFindReq.setName(idbusName);
		FindIdentityBusByNameResponse idbusFindRes = null;
		try {
			idbusFindRes = idbusMgr.findIdentityBusByName(idbusFindReq);
		}catch (Exception e) {
			throw e;
		}
        
        req.setIdentityBus(idbusFindRes.getApplinaceDefinition());
        AccountLinkagePolicy policy = new AccountLinkagePolicy();
        policy.setName("policy1");
        req.setAccountLinkagePolicy(policy);
        AuthenticationContract contract = new AuthenticationContract();
        contract.setName("contract1");
        req.setAuthenticationContract(contract);
        AuthenticationMechanism mechanism = new AuthenticationMechanism();
        mechanism.setName("mechanism1");
        req.setAuthenticationMechanism(mechanism);
        UserInformationLookup lookup = new UserInformationLookup();
        lookup.setName("lookup1");
        req.setUserInformationLookup(lookup);

        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.ACS_PROFILE);

        AddSAMLR2SPProviderResponse res = null;
        try {
            res = idbusMgr.addSAMLR2SPProvider(req);
        } catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to add service provider";
            logger.error(e);
        }
        spName = res.getServiceProvider().getName();
        
        assert res.getServiceProvider().getDisplayName().equals(SP_DISPLAY_NAME) : "Display name not set correctly";
        assert res.getServiceProvider().getDescription().equals(spDesc) : "Description not set correctly";
        assert res.getServiceProvider().getLocation().equals(spLocation) : "Location not set correctly";
        assert res.getServiceProvider().getActiveBindings().size() == 1 : "Bindings not set correctly";
        assert res.getServiceProvider().getActiveProfiles().size() == 1 : "Profiles not set correctly";
    }

    //@Test
    public void testFindSPProvider(){
        logger.debug("FindSPProvider test");
        FindSAMLR2SPProviderByNameRequest req = new FindSAMLR2SPProviderByNameRequestImpl();
        req.setName(spName);
        FindSAMLR2SPProviderByNameResponse res = null;
        try {
            res = idbusMgr.findSAMLR2SPProviderByName(req);
        } catch (IdentityApplianceMetadataElementNotFoundException e) {
            assert false : "Failed to find service provider with name: " + req.getName();
            logger.error(e);
            return;
        }
        assert res.getServiceProvider().getDisplayName().equals(SP_DISPLAY_NAME) : "Display name not persisted correctly";
        assert res.getServiceProvider().getDescription().equals("sp1 desc") : "Description not persisted correctly";
        assert res.getServiceProvider().getLocation().equals("http://localhost:8080/idbus1/sp1") : "Location not persisted correctly";
        assert res.getServiceProvider().isEncryptAuthenticationRequest() == false : "Encrypt auth request not persisted correctly";
        assert res.getServiceProvider().isSignAuthenticationRequest() == false : "Sign auth request not persisted correctly";
        assert res.getServiceProvider().getApplinaceDefinition().getName().equals(idbusName) : "Service provider's idbus not persisted correctly";
        assert res.getServiceProvider().getAccountLinkagePolicy().getName().equals("policy1") : "Policy name not persisted correctly";
        assert res.getServiceProvider().getAuthenticationContract().getName().equals("contract1") : "Contract not persisted correctly";
        assert res.getServiceProvider().getAuthenticationMechanism().getName().equals("mechanism1") : "Mechanism not persited correctly";
        assert res.getServiceProvider().getUserInformationLookup().getName().equals("lookup1") : "Lookup not persisted correctly";
        assert res.getServiceProvider().getActiveBindings().size() == 1 : "Bindings not persisted correctly";
        assert res.getServiceProvider().getActiveProfiles().size() == 1 : "Profiles not persisted correctly";
        
		Bean bean = null;
		
		try {
			bean = smm.findBeanByName("sp" + spName);
		} catch (SpringMetadataManagementException e) {
			assert false : "Failed to find service provider bean with name:" + req.getName();
			logger.error(e);
			return;
		}
		assert bean.getName().equals("sp" + spName) : "Bean name not persited correctly";        
    }

    //@Test
    public void testUpdateSPProvider() throws Exception{
        logger.debug("UpdateSPProvider test");
        UpdateSAMLR2SPProviderRequest req = new UpdateServiceProviderRequest();
        req.setName(spName);
        req.setDisplayName(SP_DISPLAY_NAME + "_updated");
        String spDesc = "sp1 desc updated";
        req.setDescription(spDesc);
        String spLocation = "http://localhost:8080/idbus1/sp1_u";
        req.setLocation(spLocation);
        req.setEncryptAuthenticationRequest(true);
        req.setSignAuthenticationRequest(true);
        
        FindIdentityBusByNameRequest idbusFindReq = new FindIdentityBusByNameRequestImpl();
        idbusFindReq.setName(idbusName);
		FindIdentityBusByNameResponse idbusFindRes = null;
		try {
			idbusFindRes = idbusMgr.findIdentityBusByName(idbusFindReq);
		}catch (Exception e) {
			throw e;
		}
        
//        req.setIdentityBus(idbusFindRes.getApplinaceDefinition());
        AccountLinkagePolicy policy = new AccountLinkagePolicy();
        policy.setName("policy1");
        req.setAccountLinkagePolicy(policy);
        AuthenticationContract contract = new AuthenticationContract();
        contract.setName("contract1");
        req.setAuthenticationContract(contract);
        AuthenticationMechanism mechanism = new AuthenticationMechanism();
        mechanism.setName("mechanism1");
        req.setAuthenticationMechanism(mechanism);
        UserInformationLookup lookup = new UserInformationLookup();
        lookup.setName("lookup1");
        req.setUserInformationLookup(lookup);

        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.ACS_PROFILE);

        UpdateSAMLR2SPProviderResponse res = null;
        try {
            res = idbusMgr.updateSAMLR2SPProvider(req);
        } catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to update service provider";
            logger.error(e);
        }
        assert res.getServiceProvider().getDisplayName().equals(SP_DISPLAY_NAME + "_updated") : "Display name not updated correctly";
        assert res.getServiceProvider().getDescription().equals(spDesc) : "Description not updated correctly";
        assert res.getServiceProvider().getLocation().equals(spLocation) : "Location not updated correctly";
        assert ((SAMLR2ServiceProvider)res.getServiceProvider()).isEncryptAuthenticationRequest() : "Encrypt auth request not updated correctly";
        assert ((SAMLR2ServiceProvider)res.getServiceProvider()).isSignAuthenticationRequest() : "Sign auth request not updated correctly";
        assert res.getServiceProvider().getActiveBindings().size() == 1 : "Bindings not updated correctly";
        assert res.getServiceProvider().getActiveProfiles().size() == 1 : "Profiles not updated correctly";
    }
    
    
    //@Test
    public void testAddIDPProvider() throws Exception{
        logger.debug("AddIDPProvider test");
        AddSAMLR2IDPProviderRequest req = new AddIdentityProviderRequest();
        req.setDisplayName(IDP_DISPLAY_NAME);
        String idpDesc = "idp1 desc";
        req.setDescription(idpDesc);
        String idpLocation = "http://localhost:8080/idbus1/idp1";
        req.setLocation(idpLocation);
        req.setEncryptAuthenticationAssertions(false);
        req.setSignAuthenticationAssertions(false);
        
        FindIdentityBusByNameRequest idbusFindReq = new FindIdentityBusByNameRequestImpl();
        idbusFindReq.setName(idbusName);
		FindIdentityBusByNameResponse idbusFindRes = null;
		try {
			idbusFindRes = idbusMgr.findIdentityBusByName(idbusFindReq);
		}catch (Exception e) {
			throw e;
		}
        
        req.setIdentityBus(idbusFindRes.getApplinaceDefinition());
        AuthenticationAssertionEmissionPolicy policy = new AuthenticationAssertionEmissionPolicy();
        policy.setName("policy1");
        req.setEmissionPolicy(policy);
        AuthenticationContract contract = new AuthenticationContract();
        contract.setName("contract1");
        req.setAuthenticationContract(contract);
        AuthenticationMechanism mechanism = new AuthenticationMechanism();
        mechanism.setName("mechanism1");
        req.setAuthenticationMechanism(mechanism);
        UserInformationLookup lookup = new UserInformationLookup();
        lookup.setName("lookup1");
        req.setUserInformationLookup(lookup);

        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.SSO_PROFILE);

        AddSAMLR2IDPProviderResponse res = null;
        try {
            res = idbusMgr.addSAMLR2IDPProvider(req);
        } catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to add identity provider";
            logger.error(e);
        }
        idpName = res.getProvider().getName();
        assert idpName != null : "Name not generated";
        
        assert res.getProvider().getDisplayName().equals(IDP_DISPLAY_NAME) : "Display name not set correctly";
        assert res.getProvider().getDescription().equals(idpDesc) : "Description not set correctly";
        assert res.getProvider().getLocation().equals(idpLocation) : "Location not set correctly";
        assert res.getProvider().getActiveBindings().size() == 1 : "Bindings not set correctly";
        assert res.getProvider().getActiveProfiles().size() == 1 : "Profiles not set correctly";
    } 
    
    //@Test
    public void testFindIDPProvider(){
        logger.debug("FindIDPProvider test");
        FindSAMLR2IDPProviderByNameRequest req = new FindSAMLR2IDPProviderByNameRequestImpl();
        req.setName(idpName);
        FindSAMLR2IDPProviderByNameResponse res = null;
        try {
            res = idbusMgr.findSAMLR2IDPProviderByName(req);
        } catch (IdentityApplianceMetadataElementNotFoundException e) {
            assert false : "Failed to find identity provider with name: " + req.getName();
            logger.error(e);
            return;
        }
        assert res.getIdentityProvider().getDisplayName().equals(IDP_DISPLAY_NAME) : "Display name not persisted correctly";
        assert res.getIdentityProvider().getDescription().equals("idp1 desc") : "Description not persisted correctly";
        assert res.getIdentityProvider().getLocation().equals("http://localhost:8080/idbus1/idp1") : "Location not persisted correctly";
        assert res.getIdentityProvider().isEncryptAuthenticationAssertions() == false : "Encrypt auth assertion not persisted correctly";
        assert res.getIdentityProvider().isSignAuthenticationAssertions() == false : "Sign auth assertion not persisted correctly";
        assert res.getIdentityProvider().getApplinaceDefinition().getName().equals(idbusName) : "Identity provider's idbus not persisted correctly";
        assert res.getIdentityProvider().getEmissionPolicy().getName().equals("policy1") : "Policy name not persisted correctly";
        assert res.getIdentityProvider().getAuthenticationContract().getName().equals("contract1") : "Contract not persisted correctly";
        assert res.getIdentityProvider().getAuthenticationMechanism().getName().equals("mechanism1") : "Mechanism not persited correctly";
        assert res.getIdentityProvider().getUserInformationLookup().getName().equals("lookup1") : "Lookup not persisted correctly";
        assert res.getIdentityProvider().getActiveBindings().size() == 1 : "Bindings not persisted correctly";
        assert res.getIdentityProvider().getActiveProfiles().size() == 1 : "Profiles not persisted correctly";
        
        Bean bean = null;
		try {
			bean = smm.findBeanByName("idp" + idpName);
		} catch (SpringMetadataManagementException e) {
			assert false : "Failed to find service provider bean with name:" + req.getName();
			logger.error(e);
			return;
		}
		assert bean.getName().equals("idp" + idpName) : "Bean name not persited correctly";              
    }
    
    //@Test
    public void testUpdateIDPProvider() throws IdentityApplianceMetadataElementNotFoundException{
    	logger.debug("AddIDPProvider test");
        UpdateIdentityProviderRequest req = new UpdateIdentityProviderRequest();
        req.setName(idpName);
        req.setDisplayName(IDP_DISPLAY_NAME + "_updated");
        String idpDesc = "idp1 desc updated";
        req.setDescription(idpDesc);
        String idpLocation = "http://localhost:8080/idbus1/idp1_u";
        req.setLocation(idpLocation);
        req.setEncryptAuthenticationAssertions(true);
        req.setSignAuthenticationAssertions(true);
        
        FindIdentityBusByNameRequest idbusFindReq = new FindIdentityBusByNameRequestImpl();
        idbusFindReq.setName(idbusName);
		FindIdentityBusByNameResponse idbusFindRes = null;
		idbusFindRes = idbusMgr.findIdentityBusByName(idbusFindReq);

        AuthenticationAssertionEmissionPolicy policy = new AuthenticationAssertionEmissionPolicy();
        policy.setName("policy1");
        req.setEmissionPolicy(policy);
        AuthenticationContract contract = new AuthenticationContract();
        contract.setName("contract1");
        req.setAuthenticationContract(contract);
        AuthenticationMechanism mechanism = new AuthenticationMechanism();
        mechanism.setName("mechanism1");
        req.setAuthenticationMechanism(mechanism);
        UserInformationLookup lookup = new UserInformationLookup();
        lookup.setName("lookup1");
        req.setUserInformationLookup(lookup);

        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.SLO_PROFILE);

        UpdateSAMLR2IDPProviderResponse res = null;
        try {
            res = idbusMgr.updateSAMLR2IDPProvider(req);
        } catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to update identity provider";
            logger.error(e);
        }    	
        
        assert res.getProvider().getDisplayName().equals(IDP_DISPLAY_NAME + "_updated") : "Display name not updated correctly";
        assert res.getProvider().getDescription().equals(idpDesc) : "Description not updated correctly";
        assert res.getProvider().getLocation().equals(idpLocation) : "Location not updated correctly";
        assert res.getProvider().isEncryptAuthenticationAssertions() == true : "Encrypt auth assertion not updated correctly";
        assert res.getProvider().isSignAuthenticationAssertions() == true : "Sign auth assertion not updated correctly";
        assert res.getProvider().getApplinaceDefinition().getName().equals(idbusName) : "Identity provider's idbus not updated correctly";
        assert res.getProvider().getEmissionPolicy().getName().equals("policy1") : "Policy name not updated correctly";
        assert res.getProvider().getAuthenticationContract().getName().equals("contract1") : "Contract not updated correctly";
        assert res.getProvider().getAuthenticationMechanism().getName().equals("mechanism1") : "Mechanism not updated correctly";
        assert res.getProvider().getUserInformationLookup().getName().equals("lookup1") : "Lookup not updated correctly";    
        assert res.getProvider().getActiveBindings().size() == 1 : "Bindings not updated correctly";
        assert res.getProvider().getActiveProfiles().size() == 1 : "Profiles not updated correctly";
    }

    //@Test
    public void testAddSPChannel() throws IdentityApplianceMetadataElementNotFoundException { //throws Exception{
    	logger.debug("AddSPChannel test");
    	AddSAMLR2SPChannelRequest req = new AddServiceProviderChannelRequest();
		req.setDisplayName(SP1_DISPLAY_CHANNEL_NAME);
    	req.setEncryptAuthRequest(true);
    	req.setSignAuthRequest(true);
    	String spChannelDescription = "sp1 channel1 desc";
		req.setDescription(spChannelDescription);
    	String spChannelLocation = "http://localhost:8080/idbus1/idp1/sp1";
		req.setLocation(spChannelLocation);
		AuthenticationAssertionEmissionPolicy policy = new AuthenticationAssertionEmissionPolicy();
        policy.setName("policy1");
    	req.setEmissionPolicy(policy);
    	AuthenticationContract contract = new AuthenticationContract();
        contract.setName("contract1");
        req.setAuthenticationContract(contract);
        AuthenticationMechanism mechanism = new AuthenticationMechanism();
        mechanism.setName("mechanism1");
        req.setAuthenticationMechanism(mechanism);
        UserInformationLookup lookup = new UserInformationLookup();
        lookup.setName("lookup1");
        req.setUserInformationLookup(lookup);
        
        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.ACS_PROFILE);
        
        FindSAMLR2IDPProviderByNameRequest idpFindReq = new FindSAMLR2IDPProviderByNameRequestImpl();
        idpFindReq.setName(idpName);
        FindSAMLR2IDPProviderByNameResponse idpFindRes = null;

		idpFindRes = idbusMgr.findSAMLR2IDPProviderByName(idpFindReq);
		
        req.setProvider(idpFindRes.getIdentityProvider());        
        req.setUseInheritedProviderSettings(false);
        AddSAMLR2SPChannelResponse res = null;
        
        try {
			res = idbusMgr.addSAMLR2SPChannel(req);
		} catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to persist sp channel";
        	logger.error(e);
		}
		spChannelName = res.getChannel().getName();
		
        assert res.getChannel().getDisplayName().equals(SP1_DISPLAY_CHANNEL_NAME) : "Display name not added correctly";
        assert res.getChannel().getDescription().equals(spChannelDescription) : "Description not added correctly";
        assert res.getChannel().getLocation().equals(spChannelLocation) : "Location not added correctly";
        assert ((SAMLR2ServiceProviderChannel)res.getChannel()).isEncryptAuthenticationRequest() : "Encrypt auth request not added correctly";
        assert ((SAMLR2ServiceProviderChannel)res.getChannel()).isSignAuthenticationRequest() : "Sign auth request not added correctly";        
        assert ((SAMLR2ServiceProviderChannel)res.getChannel()).getActiveBindings() != null : "Bindings not added correctly";
        assert ((SAMLR2ServiceProviderChannel)res.getChannel()).getActiveProfiles() != null : "Profiles not added correctly";
    }

    //@Test
    public void testFindSPChannel(){
        logger.debug("FindSPChannel test");
		FindSAMLR2SPChannelByNameRequest req = new FindSAMLR2SPChannelByNameRequestImpl();
		req.setName(spChannelName);
		FindSAMLR2SPChannelByNameResponse res = null;
		try {
			res = idbusMgr.findSAMLR2SPChannelByName(req);
		} catch (IdentityApplianceMetadataElementNotFoundException e) {
			assert false : "Failed to find sp channel with name:" + req.getName();
			logger.error(e);
			return;
		}
		assert res.getChannel().getDisplayName().equals(SP1_DISPLAY_CHANNEL_NAME) : "Display name not persisted correctly";
		assert res.getChannel().getDescription().equals("sp1 channel1 desc") : "Description not persisted correctly";
		
		String spChannelLocation = "http://localhost:8080/idbus1/idp1/sp1";
		assert res.getChannel().getLocation().equals(spChannelLocation) : "Location not persisted correctly";
		SAMLR2ServiceProviderChannel channel = res.getChannel(); 
		assert channel.isEncryptAuthenticationRequest() : "Encrypt auth request not persisted correctly";
		assert channel.isSignAuthenticationRequest() : "Sign auth request not persisted correctly";
		assert channel.getEmissionPolicy().getName().equals("policy1") : "Policy not persisted correctly";
		assert channel.getAuthenticationContract().getName().equals("contract1") : "Contract not persisted correctly";
        assert channel.getAuthenticationMechanism().getName().equals("mechanism1") : "Mechanism not persited correctly";
        assert channel.getUserInformationLookup().getName().equals("lookup1") : "Lookup not persisted correctly";
        assert channel.getProvider().getName().equals(idpName) : "Provider not persisted correctly";
        assert channel.getEndpoints().size() == 1 : "Endpoints not persisted correctly";
        assert channel.getActiveBindings().size() == 1 : "Bindings not persisted correctly";
        assert channel.getActiveProfiles().size() == 1 : "Profiles not persisted correctly";
		Bean bean = null;
		
		try {
			bean = smm.findBeanByName("sp-channel" + req.getName());
		} catch (SpringMetadataManagementException e) {
			assert false : "Failed to find sp channel bean with name: sp-channel" + req.getName();
			logger.error(e);
			return;
		}
		assert bean.getName().equals("sp-channel" + req.getName()) : "Bean name not persited correctly";
		assert bean.getClazz().equals(IDBusClassName.SAMLR2_SP_PROVIDER_CHANNEL.toString()) : "Classname not persisted correctly";
		assert beanUtils.getPropertyValue(bean, "location").equals(spChannelLocation) : "Location not persisted correctly";
//		assert beanUtils.getPropertyValue(bean, "role").equals("{urn:oasis:names:tc:SAML:2.0:springmetadata}SPSSODescriptor") : "Role not persisted correctly";
//		assert beanUtils.getPropertyRef(bean, "cot").equals("cot" + idbusName) : "Circle of Trust reference not persisted correctly";
		
		List<Bean> endpoints = beanUtils.getPropertyBeans(bean, "endpoints");
		assert endpoints.size() == 1 : "Endpoints not persisted";
    }

    //@Test
    public void testUpdateSPChannel() throws IdentityApplianceMetadataElementNotFoundException{
    	logger.debug("UpdateSPChannel test");
        UpdateSAMLR2SPChannelRequest req = new UpdateServiceProviderChannelRequest();
        req.setName(spChannelName);
        req.setDisplayName(SP1_DISPLAY_CHANNEL_NAME + "_updated");
    	req.setEncryptAuthRequest(false);
    	req.setSignAuthRequest(false);
    	String spChannelDescription = "sp1 channel1 desc updated";
		req.setDescription(spChannelDescription);
    	String spChannelLocation = "http://localhost:8080/idbus1/idp1/sp1_u";
		req.setLocation(spChannelLocation);
        
        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.SLO_PROFILE);
        
        FindSAMLR2IDPProviderByNameRequest idpFindReq = new FindSAMLR2IDPProviderByNameRequestImpl();
        idpFindReq.setName(idpName);
        FindSAMLR2IDPProviderByNameResponse idpFindRes = null;

		idpFindRes = idbusMgr.findSAMLR2IDPProviderByName(idpFindReq);
		
//        req.setProvider(idpFindRes.getIdentityProvider());        
        req.setUseInheritedProviderSettings(false);
        UpdateSAMLR2SPChannelResponse res = null;
        
        try {
			res = idbusMgr.updateSAMLR2SPChannel(req);
		} catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to persist sp channel";
        	logger.error(e);
		}
		SAMLR2ServiceProviderChannel channel = res.getChannel(); 
        assert channel.getDisplayName().equals(SP1_DISPLAY_CHANNEL_NAME + "_updated") : "Display name not updated correctly";
        assert channel.getDescription().equals(spChannelDescription) : "Description not updated correctly";
        assert channel.getLocation().equals(spChannelLocation) : "Location not updated correctly";
        assert channel.isEncryptAuthenticationRequest() == false : "Encrypt auth request not updated correctly";
        assert channel.isSignAuthenticationRequest() == false : "Sign auth request not updated correctly";
        assert channel.getEndpoints().size() == 1 : "Endpoints not updated correctly";
        assert channel.getActiveBindings().size() == 1 : "Bindings not updated correctly";
        assert channel.getActiveProfiles().size() == 1 : "Profiles not updated correctly";
        
        Bean bean = null;
        try {
			bean = smm.findBeanByName("sp-channel" + req.getName());
		} catch (SpringMetadataManagementException e) {
			assert false : "Failed to find sp channel bean with name: sp-channel" + req.getName();
			logger.error(e);
			return;
		}
		assert bean.getName().equals("sp-channel" + req.getName()) : "Bean name not updated correctly";
		assert bean.getClazz().equals(IDBusClassName.SAMLR2_SP_PROVIDER_CHANNEL.toString()) : "Classname not updated correctly";
		assert beanUtils.getPropertyValue(bean, "location").equals(spChannelLocation) : "Location not updated correctly";
//		assert beanUtils.getPropertyValue(bean, "role").equals("{urn:oasis:names:tc:SAML:2.0:springmetadata}SPSSODescriptor") : "Role not updated correctly";
//		assert beanUtils.getPropertyRef(bean, "cot").equals("cot" + idbusName) : "Circle of Trust reference not updated correctly";
		
		List<Bean> endpoints = beanUtils.getPropertyBeans(bean, "endpoints");
		assert endpoints.size() == 1 : "Endpoints not persisted";        
    }
    
    //@Test
    public void testAddIDPChannel() throws IdentityApplianceMetadataElementNotFoundException { //throws Exception{
    	logger.debug("AddIDPChannel test");
    	AddSAMLR2IDPChannelRequest req = new AddIdentityProviderChannelRequest();
		req.setDisplayName(IDP1_DISPLAY_CHANNEL_NAME);
    	req.setEncryptAuthenticationAssertions(true);
    	req.setSignAuthenticationAssertions(true);
    	String idpChannelDescription = "idp1 channel1 desc";
		req.setDescription(idpChannelDescription);
    	String idpChannelLocation = "http://localhost:8080/idbus1/sp1/idp1";
		req.setLocation(idpChannelLocation);
    	AccountLinkagePolicy policy = new AccountLinkagePolicy();
        policy.setName("policy1");
    	req.setAccountLinkagePolicy(policy);    	
    	AuthenticationContract contract = new AuthenticationContract();
        contract.setName("contract1");
        req.setAuthenticationContract(contract);
        AuthenticationMechanism mechanism = new AuthenticationMechanism();
        mechanism.setName("mechanism1");
        req.setAuthenticationMechanism(mechanism);
        UserInformationLookup lookup = new UserInformationLookup();
        lookup.setName("lookup1");
        req.setUserInformationLookup(lookup);
        
        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.SSO_PROFILE);
        
        FindSAMLR2SPProviderByNameRequest spFindReq = new FindSAMLR2SPProviderByNameRequestImpl();
        spFindReq.setName(spName);
        FindSAMLR2SPProviderByNameResponse idpFindRes = null;

		idpFindRes = idbusMgr.findSAMLR2SPProviderByName(spFindReq);
		
        req.setProvider(idpFindRes.getServiceProvider());        
        req.setUseInheritedProviderSettings(false);
        AddSAMLR2IDPChannelResponse res = null;
        
        try {
			res = idbusMgr.addSAMLR2IDPChannel(req);
		} catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to persist idp channel";
        	logger.error(e);
		}
		idpChannelName = res.getChannel().getName();
		
		assert res.getChannel().getDisplayName().equals(IDP1_DISPLAY_CHANNEL_NAME) : "Display name not persisted correctly";
		assert res.getChannel().getDescription().equals(idpChannelDescription) : "Description not persisted correctly";
				
		assert res.getChannel().getLocation().equals(idpChannelLocation) : "Location not persisted correctly";
		SAMLR2IdentityProviderChannel channel = res.getChannel(); 
		assert channel.isEncryptAuthenticationAssertions() : "Encrypt auth assertion not persisted correctly";
		assert channel.isSignAuthenticationAssertions() : "Sign auth assertion not persisted correctly";
		assert channel.getAccountLinkagePolicy().getName().equals("policy1") : "Policy not persisted correctly";
		assert channel.getAuthenticationContract().getName().equals("contract1") : "Contract not persisted correctly";
        assert channel.getAuthenticationMechanism().getName().equals("mechanism1") : "Mechanism not persited correctly";
        assert channel.getUserInformationLookup().getName().equals("lookup1") : "Lookup not persisted correctly";
        assert channel.getProvider().getName().equals(spName) : "Provider not persisted correctly";
        assert channel.getEndpoints().size() == 1 : "Endpoints not persisted correctly"; 
        assert channel.getActiveBindings().size() == 1 : "Bindings not persisted correctly";
        assert channel.getActiveProfiles().size() == 1 : "Profiles not persisted correctly";
    }  
    
    //@Test
    public void testFindIDPChannel(){
        logger.debug("FindIDPChannel test");
		FindSAMLR2IDPChannelByNameRequest req = new FindSAMLR2IDPChannelByNameRequestImpl();
		req.setName(idpChannelName);
		FindSAMLR2IDPChannelByNameResponse res = null;
		try {
			res = idbusMgr.findSAMLR2IDPChannelByName(req);
		} catch (IdentityApplianceMetadataElementNotFoundException e) {
			assert false : "Failed to find idp channel with name:" + req.getName();
			logger.error(e);
			return;
		}
		assert res.getChannel().getDisplayName().equals(IDP1_DISPLAY_CHANNEL_NAME) : "Name not persisted correctly";
		assert res.getChannel().getDescription().equals("idp1 channel1 desc") : "Description not persisted correctly";
		
		String idpChannelLocation = "http://localhost:8080/idbus1/sp1/idp1";
		assert res.getChannel().getLocation().equals(idpChannelLocation) : "Location not persisted correctly";
		SAMLR2IdentityProviderChannel channel = res.getChannel(); 
		assert channel.isEncryptAuthenticationAssertions() : "Encrypt auth assertion not persisted correctly";
		assert channel.isSignAuthenticationAssertions() : "Sign auth assertion not persisted correctly";
		assert channel.getAccountLinkagePolicy().getName().equals("policy1") : "Policy not persisted correctly";
		assert channel.getAuthenticationContract().getName().equals("contract1") : "Contract not persisted correctly";
        assert channel.getAuthenticationMechanism().getName().equals("mechanism1") : "Mechanism not persited correctly";
        assert channel.getUserInformationLookup().getName().equals("lookup1") : "Lookup not persisted correctly";
        assert channel.getProvider().getName().equals(spName) : "Provider not persisted correctly";
        assert channel.getEndpoints().size() == 1 : "Endpoints not persisted correctly";
        assert channel.getActiveBindings().size() == 1 : "Bindings not persisted correctly";
        assert channel.getActiveProfiles().size() == 1 : "Profiles not persisted correctly";
		Bean bean = null;
		
		try {
			bean = smm.findBeanByName("idp-channel" + req.getName());
		} catch (SpringMetadataManagementException e) {
			assert false : "Failed to find idp channel bean with name: idp-channel" + req.getName();
			logger.error(e);
			return;
		}
		assert bean.getName().equals("idp-channel" + req.getName()) : "Bean name not persited correctly";
		assert bean.getClazz().equals(IDBusClassName.SAMLR2_IDP_PROVIDER_CHANNEL.toString()) : "Classname not persisted correctly";
		assert beanUtils.getPropertyValue(bean, "location").equals(idpChannelLocation) : "Location not persisted correctly";
//		assert beanUtils.getPropertyValue(bean, "role").equals("{urn:oasis:names:tc:SAML:2.0:springmetadata}IDPSSODescriptor") : "Role not persisted correctly";
//		assert beanUtils.getPropertyRef(bean, "cot").equals("cot" + idbusName) : "Circle of Trust reference not persisted correctly";
		
		List<Bean> endpoints = beanUtils.getPropertyBeans(bean, "endpoints");
		assert endpoints.size() == 1 : "Endpoints not persisted";
    }    
    
    //@Test
    public void testUpdateIDPChannel() throws IdentityApplianceMetadataElementNotFoundException{
    	logger.debug("UpdateIDPChannel test");
        UpdateSAMLR2IDPChannelRequest req = new UpdateIdentityProviderChannelRequest();
        req.setName(idpChannelName);
        req.setDisplayName(IDP1_DISPLAY_CHANNEL_NAME + "_updated");
    	req.setEncryptAuthenticationAssertions(false);
    	req.setSignAuthenticationAssertions(false);
    	String idpChannelDescription = "idp1 channel1 desc updated";
		req.setDescription(idpChannelDescription);
    	String idpChannelLocation = "http://localhost:8080/idbus1/sp1/idp1_u";
		req.setLocation(idpChannelLocation);
        
        req.getActiveBindings().add(Binding.SAMLR2_HTTP_POST);
        req.getActiveProfiles().add(Profile.SLO_PROFILE);
        
        FindSAMLR2SPProviderByNameRequest spFindReq = new FindSAMLR2SPProviderByNameRequestImpl();
        spFindReq.setName(spName);
        FindSAMLR2SPProviderByNameResponse spFindRes = null;

		spFindRes = idbusMgr.findSAMLR2SPProviderByName(spFindReq);
		
//        req.setProvider(spFindRes.getServiceProvider());        
        req.setUseInheritedProviderSettings(false);
        UpdateSAMLR2IDPChannelResponse res = null;
        
        try {
			res = idbusMgr.updateSAMLR2IDPChannel(req);
		} catch (IdentityApplianceMetadataManagementException e) {
            assert false : "failed to persist sp channel";
        	logger.error(e);
		}
		SAMLR2IdentityProviderChannel channel = res.getChannel(); 
        assert channel.getDisplayName().equals(IDP1_DISPLAY_CHANNEL_NAME + "_updated") : "name not updated correctly";
        assert channel.getDescription().equals(idpChannelDescription) : "Description not updated correctly";
        assert channel.getLocation().equals(idpChannelLocation) : "Location not updated correctly";
        assert channel.isEncryptAuthenticationAssertions() == false : "Encrypt auth assertions not updated correctly";
        assert channel.isSignAuthenticationAssertions() == false : "Sign auth assertions not updated correctly";
        assert channel.getEndpoints().size() == 1 : "Endpoints not updated correctly";
        assert channel.getActiveBindings().size() == 1 : "Bindings not updated correctly";
        assert channel.getActiveProfiles().size() == 1 : "Profiles not updated correctly";
        
        Bean bean = null;
        try {
			bean = smm.findBeanByName("idp-channel" + req.getName());
		} catch (SpringMetadataManagementException e) {
			assert false : "Failed to find idp channel bean with name: idp-channel" + req.getName();
			logger.error(e);
			return;
		}
		assert bean.getName().equals("idp-channel" + req.getName()) : "Bean name not updated correctly";
		assert bean.getClazz().equals(IDBusClassName.SAMLR2_IDP_PROVIDER_CHANNEL.toString()) : "Classname not updated correctly";
		assert beanUtils.getPropertyValue(bean, "location").equals(idpChannelLocation) : "Location not updated correctly";
//		assert beanUtils.getPropertyValue(bean, "role").equals("{urn:oasis:names:tc:SAML:2.0:springmetadata}IDPSSODescriptor") : "Role not updated correctly";
//		assert beanUtils.getPropertyRef(bean, "cot").equals("cot" + idbusName) : "Circle of Trust reference not updated correctly";
		
		List<Bean> endpoints = beanUtils.getPropertyBeans(bean, "endpoints");
		assert endpoints.size() == 1 : "Endpoints not persisted";        
    }
    
    //@Test
    public void testReadChildElements() throws IdentityApplianceMetadataElementNotFoundException{
    	FindIdentityBusByNameRequest idbusReq = new FindIdentityBusByNameRequestImpl();
    	idbusReq.setName(idbusName);
    	FindIdentityBusByNameResponse idbusRes = idbusMgr.findIdentityBusByName(idbusReq);
    	IdentityApplianceDefinition idbus = idbusRes.getApplinaceDefinition();
    	assert idbus.getProviders() != null && idbus.getProviders().size() == 2 : "Failed to add providers to idbus";
    	
    	FindSAMLR2IDPProviderByNameRequest idpReq = new FindSAMLR2IDPProviderByNameRequestImpl();
    	idpReq.setName(idpName);
    	FindSAMLR2IDPProviderByNameResponse idpRes = idbusMgr.findSAMLR2IDPProviderByName(idpReq);
    	SAMLR2IdentityProvider idp = idpRes.getIdentityProvider();
    	assert idp.getChannels() != null && idp.getChannels().size() == 1 : "Failed to add channel to identity provider";
    	
    	FindSAMLR2SPProviderByNameRequest spReq = new FindSAMLR2SPProviderByNameRequestImpl();
    	spReq.setName(spName);
    	FindSAMLR2SPProviderByNameResponse spRes = idbusMgr.findSAMLR2SPProviderByName(spReq);
    	SAMLR2ServiceProvider sp = spRes.getServiceProvider();
    	assert sp.getChannels() != null && sp.getChannels().size() == 1 : "Failed to add channel to service provider";    	
    	
    }
    
    //@Test
    public void testDeleteSPChannel(){
		RemoveSAMLR2SPChannelRequest req = new RemoveServiceProviderChannelRequest();
		req.setName(spChannelName);
		try {
			idbusMgr.removeSAMLR2SPChannel(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "failed to delete sp channel";
			logger.error(e);
		} 
    }
    
    //@Test
    public void testDeleteIDPChannel(){
		RemoveSAMLR2IDPChannelRequest req = new RemoveIdentityProviderChannelRequest();
		req.setName(idpChannelName);
		try {
			idbusMgr.removeSAMLR2IDPChannel(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "failed to delete idp channel";
			logger.error(e);
		} 
    }

    //@Test
    public void testDeleteSPProvider(){
		RemoveSAMLR2SPProviderRequest req = new RemoveServiceProviderRequest();
		req.setName(spName);
		try {
			idbusMgr.removeSAMLR2SPProvider(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "failed to delete service provider";
			logger.error(e);
		} 
    }
   
    //@Test
    public void testDeleteIDPProvider(){
		RemoveSAMLR2IDPProviderRequest req = new RemoveIdentityProviderRequest();
		req.setName(idpName);
		try {
			idbusMgr.removeSAMLR2IDPProvider(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "failed to delete identity provider";
			logger.error(e);
		}
    }
    
    //@Test
    public void findDeletedSPProvider(){
		FindSAMLR2SPProviderByNameRequest findReq = new FindSAMLR2SPProviderByNameRequestImpl();
	    findReq.setName(spName);
	     FindSAMLR2SPProviderByNameResponse res = null;
	     try {
	         res = idbusMgr.findSAMLR2SPProviderByName(findReq);
	     } catch (IdentityApplianceMetadataElementNotFoundException e) {
	         assert true : "Service provider with name: " + findReq.getName() + " deleted correctly";
	         logger.error(e);
	         return;
	     }    	
    }
    
    //@Test
    public void findDeletedIDPProvider(){
		FindSAMLR2IDPProviderByNameRequest findReq = new FindSAMLR2IDPProviderByNameRequestImpl();
	    findReq.setName(idpName);
	     FindSAMLR2IDPProviderByNameResponse res = null;
	     try {
	         res = idbusMgr.findSAMLR2IDPProviderByName(findReq);
	     } catch (IdentityApplianceMetadataElementNotFoundException e) {
	     	 assert true : "Identity provider with name: " + findReq.getName() + " deleted correctly";
	         logger.error(e);
	         return;
	     }    	
    }
    
    
	//@Test
	public void testDeleteIdentityBus(){
		RemoveIdentityApplianceDefinitionRequest req = new RemoveIdentityApplianceDefinitionRequest();
		
		req.setName(idbusName);
		
		try {
			idbusMgr.removeIdentityBus(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "failed to delete identity bus";
			logger.error(e);
		} 
	}

    //@Test
    public void findDeletedIDBus(){
		FindIdentityBusByNameRequest findReq = new FindIdentityBusByNameRequestImpl();
	    findReq.setName(idbusName);
	     FindIdentityBusByNameResponse res = null;
	     try {
	         res = idbusMgr.findIdentityBusByName(findReq);
	     } catch (IdentityApplianceMetadataElementNotFoundException e) {
     	 	 assert true : "Identity bus with name: " + findReq.getName() + " deleted correctly";	     
	         logger.error(e);
	         return;
	     }    	
    }
    
    public void testGenerateSPChannelMetadataDescriptor(){
    	GenerateSAMLR2SPChannelMetadataDescriptorRequest req = new GenerateSAMLR2SPChannelMetadataDescriptorRequestImpl();
    	req.setName(spChannelName);
    	GenerateSAMLR2SPChannelMetadataDescriptorResponse res = null;
    	
    	try {
			res = idbusMgr.generateSAMLR2SPChannelMetadataDescriptor(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "Failed to generate SP channel metadata descriptor";
			logger.error(e);
		}
    }
    
    public void testGenerateIDPChannelMetadataDescriptor(){
    	GenerateSAMLR2IDPChannelMetadataDescriptorRequest req = new GenerateSAMLR2IDPChannelMetadataDescriptorRequestImpl();
    	req.setName(idpChannelName);
    	GenerateSAMLR2IDPChannelMetadataDescriptorResponse res = null;
    	
    	try {
			res = idbusMgr.generateSAMLR2IDPChannelMetadataDescriptor(req);
		} catch (IdentityApplianceMetadataManagementException e) {
			assert false : "Failed to generate IDP channel metadata descriptor";
			logger.error(e);
		}
    }

        */
}
