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

package com.atricore.idbus.console.lifecycle.main.test;

import java.util.Map;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jdo.*;

public class PersistenceTest {

    private static final Log logger = LogFactory.getLog( PersistenceTest.class );

    private static PersistenceManagerFactory pmf;

    private static ApplicationContext applicationContext;

    @BeforeClass
    public static void setupTestSuite() {

        applicationContext =
                new ClassPathXmlApplicationContext("/com/atricore/idbus/console/lifecycle/main/test/persistence-test-beans.xml");

        Map<String, PersistenceManagerFactory> pmfs = applicationContext.getBeansOfType(PersistenceManagerFactory.class);
        assert pmfs != null && pmfs.size() == 1 : "Too many/few PMFs found : " + pmfs;

        pmf = pmfs.values().iterator().next();

        logger.debug("Using PMF " + pmf);
    }

    @AfterClass
    public static void tearDownClass() {

        String strTestTimeout= System.getProperty("com.atricore.test.timeout");
        if (strTestTimeout != null) {
            long timeout = Long.parseLong(strTestTimeout);
            synchronized (Thread.currentThread()) {
                logger.info("Waiting for Test " + timeout + " ms (0 waist for ever  ...)");
                try { Thread.currentThread().wait(timeout); } catch (InterruptedException e) { /**/ }
            }
        }

        logger.info("Shutting down test suite");

        if (pmf != null)
            pmf.close();
        
        pmf = null;
        applicationContext = null;
    }


    @Test
    public void testPersistApliance() throws Exception{

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        try {
            tx.begin();

            IdentityAppliance idbus = (IdentityAppliance) applicationContext.getBean("ida1");
            pm.makePersistent(idbus);
            
            tx.commit();
        } catch (Exception e){
            logger.error("Error persisting Identity Bus", e);
            throw e;
        } finally {
            if (tx.isActive())
                tx.rollback();
            pm.close();
        }
    }

    /*
    @Test
    public void testRetrieveAppliance() throws Exception{

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx=pm.currentTransaction();
        IdentityAppliance ida = null;
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
            test = ia.getIdApplianceDefinition();
        } else {
            assert false: "Identity Appliance Definition not read!";
        }

        //we want to test detached objects, not the one in transaction
        IdentityApplianceDefinition original  = (IdentityApplianceDefinition) appCtx.getBean("idbus1");

        assertApplianceDefinitionsAreEqual(original, test);
    }

    // TODO : Test also Identity Appliance

    @Test
    public void testFetchingStartedAppliances(){
        // TODO
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
            q.declareImports("import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment");
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

    */

    protected void assertAppliancesAreEqual(IdentityAppliance original, IdentityAppliance test) {
        TestCase.assertNotNull(original);
        TestCase.assertNotNull(test);

        TestCase.assertEquals(original.getId(), test.getId());
        TestCase.assertEquals(original.getState(), test.getState());

        assertApplianceDefinitionsAreEqual(original.getIdApplianceDefinition(), test.getIdApplianceDefinition());
        assertAppliaceDeploymentsAreEqual(original.getIdApplianceDeployment(), test.getIdApplianceDeployment());

    }

    protected void assertAppliaceDeploymentsAreEqual(IdentityApplianceDeployment original, IdentityApplianceDeployment test) {
        TestCase.assertTrue((original == null && test == null) || (original != null && test != null));
        if (original == null)
            return;

        TestCase.assertEquals(original.getId(), test.getId());
        TestCase.assertEquals(original.getDeployedRevision(), test.getDeployedRevision());
        TestCase.assertEquals(original.getDeploymentTime(), test.getDeploymentTime());
        TestCase.assertEquals(original.getDescription(), test.getDescription());
        TestCase.assertEquals(original.getFeatureName(), test.getFeatureName());
        TestCase.assertEquals(original.getFeatureUri(), test.getFeatureUri());
        TestCase.assertEquals(original.getState(), test.getState());

        // TODO IDAUs



    }

    protected void assertApplianceDefinitionsAreEqual(IdentityApplianceDefinition original, IdentityApplianceDefinition test) {
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
            assert found : "Provider " + originalProvider.getName() + " not found";
        }

    }


    protected void assertProvidersAreEqual(Provider original, Provider test) {
        TestCase.assertEquals(original.getName(), test.getName());
        TestCase.assertEquals(original.getDescription(), test.getDescription());
        TestCase.assertEquals(original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        TestCase.assertEquals(original.getClass().getName(), test.getClass().getName());

        if (original instanceof LocalProvider) {
            LocalProvider originalL = (LocalProvider) original;
            LocalProvider testL = (LocalProvider) test;
            assertConfigsAreEqual(originalL.getConfig(), testL.getConfig());

            if (originalL instanceof ServiceProvider) {

                ServiceProvider originalSp = (ServiceProvider) originalL;
                ServiceProvider testSp = (ServiceProvider) testL;

                assertIdenityLookupsAreEqual(originalSp.getIdentityLookup(), testSp.getIdentityLookup());
                assertActivationsAreEqual(originalSp.getActivation(), testSp.getActivation());
                for (FederatedConnection originalC : originalSp.getFederatedConnections()) {

                    boolean found = false;
                    for (FederatedConnection testC : testSp.getFederatedConnections()) {
                        if (originalC.getId() == testC.getId()) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC);
                            break;

                        }
                    }

                    TestCase.assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }

            } else if (originalL instanceof IdentityProvider) {
                IdentityProvider originalIdp = (IdentityProvider) originalL;
                IdentityProvider testIdp = (IdentityProvider) testL;

                assertIdenityLookupsAreEqual(originalIdp.getIdentityLookup(), testIdp.getIdentityLookup());
                for (FederatedConnection originalC : originalIdp.getFederatedConnections()) {

                    boolean found = false;
                    for (FederatedConnection testC : testIdp.getFederatedConnections()) {
                        if (originalC.getId() == testC.getId()) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC);
                            break;
                        }
                    }

                    TestCase.assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }

            } else if (originalL instanceof ProvisioningServiceProvider) {
                // TODO :
            }


        } else if (original instanceof RemoteProvider) {
            RemoteProvider originalR = (RemoteProvider) original;
            RemoteProvider testR = (RemoteProvider) test;

            assertResourcesAreEqual(originalR.getMetadata(), testR.getMetadata());
        }
    }

    protected void assertIdenityLookupsAreEqual(IdentityLookup original, IdentityLookup test) {
        // TODO :
    }

    protected void assertActivationsAreEqual(Activation original, Activation test) {
        // TODO :
    }

    protected void assertFederatedConnectionsAreEqual(FederatedConnection original, FederatedConnection test) {
        // TODO :
    }

    protected void assertChannelsAreEqual(Channel original, Channel test) {
        TestCase.assertEquals( original.getName(), test.getName());
        TestCase.assertEquals( original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        TestCase.assertEquals( original.getClass().getName(), test.getClass().getName());
        TestCase.assertEquals( original.isOverrideProviderSetup(), test.isOverrideProviderSetup());

        if (original instanceof IdentityProviderChannel) {
            IdentityProviderChannel originalIdP = (IdentityProviderChannel) original;
            originalIdP.getAccountLinkagePolicy(); // TODO
            // TODO : assertIdentityVaultsAreEqual(originalIdP.getIdentityVault(), ((IdentityProviderChannel)test).getIdentityVault());
        } else if (original instanceof ServiceProviderChannel) {
            ServiceProviderChannel originalSP = (ServiceProviderChannel) original;
            originalSP.getEmissionPolicy(); // TODO
        }

    }

    private void assertIdentityVaultsAreEqual(IdentitySource original, IdentitySource test) {
        DbIdentitySource dbOriginal = (DbIdentitySource) original;
        DbIdentitySource dbTest = (DbIdentitySource) test;
        TestCase.assertEquals(dbOriginal.getName(), dbTest.getName());
        TestCase.assertEquals(dbOriginal.getAdmin(), dbTest.getAdmin());
        TestCase.assertEquals(dbOriginal.getPassword(), dbTest.getPassword());
        TestCase.assertEquals(dbOriginal.getPort(), dbTest.getPort());
    }

    protected void assertConfigsAreEqual(ProviderConfig original, ProviderConfig test) {
        // TODO :
    }

    protected void assertResourcesAreEqual(Resource original, Resource test) {
        // TODO :
    }


}
