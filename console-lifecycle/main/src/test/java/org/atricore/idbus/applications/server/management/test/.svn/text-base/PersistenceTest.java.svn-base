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

package org.atricore.idbus.applications.server.management.test;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.main.domain.IdentityAppliance;
import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceDeployment;
import org.atricore.idbus.capabilities.management.main.domain.metadata.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jdo.*;

public class PersistenceTest {

	private static final String EMISSION_POLICY_NAME = "emissionPolicy1_1";
	private static final String INFO_LOOKUP_NAME = "infoLookup1_1";
	private static final String AUTH_CONTRACT_NAME = "authContract1_1";
	private static final String AUTH_MECHANISM_NAME = "authMechanism1_1";
	private static final String IDP_CHANNEL_NAME = "idpChannel1_1";
	private static final String IDP_NAME = "idp1_1";
	private static final String IDBUS_NAME = "idbus1_1";

	private static final Log logger = LogFactory.getLog( PersistenceTest.class );

    private PersistenceManagerFactory pmf;

    private ApplicationContext appCtx;

    @Before
    public void setUp() {
        pmf = JDOHelper.getPersistenceManagerFactory("datanucleus-tests.properties");
        appCtx = new ClassPathXmlApplicationContext("org/atricore/idbus/applications/server/management/test/persistence-test-beans.xml");
    }

	
    @Test
    public void testPersistDomain() throws Exception{

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try
        {
            tx.begin();
            
            IdentityApplianceDefinition idbus = (IdentityApplianceDefinition) appCtx.getBean("idbus1");
            IdentityAppliance idAppliance = new IdentityAppliance();
            idAppliance.setIdApplianceDefinition(idbus);
            
            pm.makePersistent(idAppliance);
            
            tx.commit();
        }catch (Exception e){
            logger.error("Error persisting Identity Bus", e);
            throw e;
        }finally {
            if (tx.isActive()){
                tx.rollback();
            }
            pm.close();
        }
    }
    
    @Test
    public void testReadDomain() throws Exception{

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        IdentityApplianceDefinition idbus = null;
        IdentityAppliance ia = null;
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(5);
            Extent e = pm.getExtent(IdentityAppliance.class, true);
            Iterator iter = e.iterator();

            if(!iter.hasNext()) assert false : "No Identity Bus persisted";

            while (iter.hasNext()){
                Object obj = iter.next();
                if(obj instanceof IdentityAppliance){
                	ia = pm.detachCopy((IdentityAppliance)obj);

                }
            }        
            
            e.closeAll();

            tx.commit();
        }catch (Exception e){
            logger.error("Error reading Domain objects", e);
            throw e;
        }finally {
            if (tx.isActive()){
                tx.rollback();
            }
            pm.close();
        }
        if(ia.getIdApplianceDefinition() != null){
            idbus = ia.getIdApplianceDefinition();
        } else {
            assert false: "Identity Appliance Definition not read!";
        }

        //we want to test detached objects, not the one in transaction
        IdentityApplianceDefinition idbusOriginal = (IdentityApplianceDefinition) appCtx.getBean("idbus1");

        assertIDBusesAreEqual(idbusOriginal, idbus);
    }

    @Test
    public void testFetchingStartedAppliances(){
        //TODO
    }

//    @Test
    public void testFetchingProjectedAppliances() throws Exception {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        Collection result = null;
        try {
            tx.begin();
//            pm.getObject
            Query subquery = pm.newQuery(IdentityApplianceDeployment.class, "state == \"Started\"");
            Query q = pm.newQuery(IdentityAppliance.class, "deployments.contains(idApplianceDeployment)");
            q.declareImports("import org.atricore.idbus.capabilities.management.main.domain.IdentityApplianceDeployment");
            q.addSubquery(subquery, "Collection deployments", null);
            result = pm.detachCopyAll((Collection)q.execute());
//            result = pm.detachCopyAll((Collection)subquery.execute());


        }catch (Exception e){
            logger.error("Error reading projected appliances", e);
            throw e;
        }finally {
            if (tx.isActive()){
                tx.rollback();
            }
            pm.close();
        }
        assert !result.isEmpty() : "no appliance found";
    }
    
    @Test
    public void testDeleteDomain() throws Exception{

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();
            pm.getFetchPlan().setMaxFetchDepth(4);
            Extent e = pm.getExtent(IdentityAppliance.class, true);
            Iterator iter = e.iterator();

            IdentityAppliance appliance = null;
            if(iter.hasNext()){
                appliance = (IdentityAppliance)iter.next();
            } else {
                throw new Exception("no appliance found");
            }
//            pm.deletePersistent(appliance.getIdApplianceDefinition());
            appliance.getIdApplianceDefinition().setProviders(null);
            pm.makePersistent(appliance);
//            tx.commit();
//            pm.close();
//            pm = pmf.getPersistenceManager();
//            tx=pm.currentTransaction();
//            tx.begin();
//            e = pm.getExtent(IdentityAppliance.class, true);
//            iter = e.iterator();
//
//            if(iter.hasNext()){
//                appliance = (IdentityAppliance)iter.next();
                pm.deletePersistent(appliance);
//            }
            logger.debug("Deleted identity appliance");
            tx.commit();

        }catch (Exception e){
            logger.error("Error reading Domain objects", e);
            throw e;
        }finally {
            if (tx.isActive()){
                tx.rollback();
            }
            pm.close();
        }     	
    }

    protected void assertIDBusesAreEqual(IdentityApplianceDefinition original, IdentityApplianceDefinition test) {
        TestCase.assertEquals( original.getName(), test.getName()) ;
        TestCase.assertEquals( original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString()) ;

        TestCase.assertEquals( original.getProviders().size() , test.getProviders().size());

        for (Provider originalProvider : original.getProviders()) {
            boolean found = false;
            for (Provider testProvider : test.getProviders()) {
                if (originalProvider.getName().equals(testProvider.getName())) {
                    assertProvidersAreEqual(originalProvider, testProvider);
                    found = true;
                }
            }
            assert found : "Provider not found";
        }

    }


    protected void assertProvidersAreEqual(Provider original, Provider test) {
        TestCase.assertEquals(original.getName(), test.getName());
        TestCase.assertEquals(original.getDescription(), test.getDescription());
//        TestCase.assertEquals(original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        TestCase.assertEquals(original.getClass().getName(), test.getClass().getName());



//        TestCase.assertEquals(original.getActiveBindings().size(), test.getActiveBindings().size());
//        for(Binding originalB : original.getActiveBindings()) {
//            assert test.getActiveBindings().contains(originalB);
//        }

        if (original instanceof LocalProvider) {
            LocalProvider originalL = (LocalProvider) original;
            LocalProvider testL = (LocalProvider) test;

            TestCase.assertEquals(originalL.getChannels().size() ,testL.getChannels().size());

            assertChannelsAreEqual(originalL.getDefaultChannel(), testL.getDefaultChannel());
            assertConfigsAreEqual(originalL.getConfig(), testL.getConfig());

            for (Channel originalC : originalL.getChannels()) {
                boolean found = false;
                for (Channel testC : testL.getChannels()) {
                    if (originalC.getName().equals(testC.getName())) {
                        found = true;
                        assertChannelsAreEqual(originalC, testC);
                    }
                }
                assert found : "Channel not found";
            }

        }


    }

    protected void assertChannelsAreEqual(Channel original, Channel test) {
        TestCase.assertEquals( original.getName(), test.getName());
        TestCase.assertEquals( original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        TestCase.assertEquals( original.getClass().getName(), test.getClass().getName());
        if (original.getTarget() != null) {
            TestCase.assertNotNull(test.getTarget());
            TestCase.assertEquals(original.getTarget().getName(), test.getTarget().getName());
        } else {
            TestCase.assertNull( test.getTarget() );
        }

        if (original instanceof BindingChannel) {
            BindingChannel originalB = (BindingChannel) original;
            BindingChannel testB = (BindingChannel) test;

        } else if (original instanceof IdentityProviderChannel) {
            IdentityProviderChannel originalIdP = (IdentityProviderChannel) original;
            originalIdP.getAccountLinkagePolicy(); // TODO
            assertIdentityVaultsAreEqual(originalIdP.getIdentityVault(), ((IdentityProviderChannel)test).getIdentityVault());
        } else if (original instanceof ServiceProviderChannel) {
            ServiceProviderChannel originalSP = (ServiceProviderChannel) original;
            originalSP.getEmissionPolicy(); // TODO
        }



    }

    private void assertIdentityVaultsAreEqual(IdentityVault original, IdentityVault test) {
        DbIdentityVault dbOriginal = (DbIdentityVault) original;
        DbIdentityVault dbTest = (DbIdentityVault) test;
        TestCase.assertEquals(dbOriginal.getName(), dbTest.getName());
        TestCase.assertEquals(dbOriginal.getAdmin(), dbTest.getAdmin());
        TestCase.assertEquals(dbOriginal.getPassword(), dbTest.getPassword());
        TestCase.assertEquals(dbOriginal.getPort(), dbTest.getPort());
    }

    protected void assertConfigsAreEqual(ProviderConfig original, ProviderConfig test) {

    }
}
