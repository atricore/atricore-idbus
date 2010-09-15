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

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import com.atricore.idbus.console.lifecycle.main.spi.request.AddIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.DisposeIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.RemoveIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.request.UpdateIdentityApplianceRequest;
import com.atricore.idbus.console.lifecycle.main.spi.response.AddIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.DisposeIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.RemoveIdentityApplianceResponse;
import com.atricore.idbus.console.lifecycle.main.spi.response.UpdateIdentityApplianceResponse;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class PersistenceTest {

    private static final Log logger = LogFactory.getLog( PersistenceTest.class );

    private static ApplicationContext applicationContext;

    private IdentityApplianceManagementService svc;

    @BeforeClass
    public static void setupTestSuite() {
        applicationContext =
                new ClassPathXmlApplicationContext("/com/atricore/idbus/console/lifecycle/main/test/persistence-test-beans.xml");
    }

    @Before
    public void setupTest() {
        Map<String, IdentityApplianceManagementService> svcs = applicationContext.getBeansOfType(IdentityApplianceManagementService.class);
        assert svcs != null && svcs.size() == 1 : "Too many/few IdentityApplianceManagementService definitions found : " + svcs;
        svc = svcs.values().iterator().next();
    }

    @AfterClass
    public static void tearDownTestSuite() {

        String strTestTimeout= System.getProperty("com.atricore.test.waitFor");
        if (strTestTimeout != null) {
            long timeout = Long.parseLong(strTestTimeout);
            synchronized (Thread.currentThread()) {
                logger.info("Waiting for Test " + timeout + " ms (0 waits for ever  ...)");
                System.out.println("Waiting for Test " + timeout + " ms (0 waits for ever  ...)");
                try { Thread.currentThread().wait(timeout); } catch (InterruptedException e) { /**/ }
            }
        }

        logger.info("Shutting down test suite");
        applicationContext = null;
    }

    @After
    public void tearDownTest() {
    }

    //@Test
    public void testAddAppliance() throws Exception{

        IdentityAppliance ida1 = newApplianceInstance("ida1");

        AddIdentityApplianceRequest req = new AddIdentityApplianceRequest();
        req.setIdentityAppliance(ida1);

        AddIdentityApplianceResponse res =  svc.addIdentityAppliance(req);
        IdentityAppliance ida1Test = res.getAppliance();

        ida1 = newApplianceInstance("ida1");

        // Let's check that we get what we sent !
        assertAppliancesAreEqual(ida1, ida1Test, true);
    }

    //@Test
    public void testUpdaetAppliance() throws Exception {
        IdentityAppliance ida1 = newApplianceInstance("ida1");

        AddIdentityApplianceRequest addReq = new AddIdentityApplianceRequest();
        addReq.setIdentityAppliance(ida1);

        AddIdentityApplianceResponse addRes =  svc.addIdentityAppliance(addReq);
        IdentityAppliance ida1Test = addRes.getAppliance();

        ida1 = newApplianceInstance("ida1");

        UpdateIdentityApplianceRequest updReq = new UpdateIdentityApplianceRequest();
        updReq.setAppliance(ida1Test);

        UpdateIdentityApplianceResponse updRes = svc.updateIdentityAppliance(updReq);
        ida1Test = updRes.getAppliance();

        // Let's check that we get what we sent !
        assertAppliancesAreEqual(ida1, ida1Test, true);

    }

    @Test
    public void testDeleteAppliance() throws Exception{

        IdentityAppliance ida1 = newApplianceInstance("ida1");

        AddIdentityApplianceRequest req = new AddIdentityApplianceRequest();
        req.setIdentityAppliance(ida1);

        AddIdentityApplianceResponse res =  svc.addIdentityAppliance(req);
        IdentityAppliance ida1Test = res.getAppliance();

        ida1 = newApplianceInstance("ida1");

        // Let's check that we get what we sent !
        assertAppliancesAreEqual(ida1, ida1Test, true);


        DisposeIdentityApplianceRequest disposeReq = new DisposeIdentityApplianceRequest();
        disposeReq.setId(ida1Test.getId() + "");
        DisposeIdentityApplianceResponse disposeRes = svc.disposeIdentityAppliance(disposeReq);

        RemoveIdentityApplianceRequest removeReq = new RemoveIdentityApplianceRequest();
        removeReq.setApplianceId(ida1Test.getId() + "");

        RemoveIdentityApplianceResponse removeRes = svc.removeIdentityAppliance(removeReq);

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

    protected void assertAppliancesAreEqual(IdentityAppliance original, IdentityAppliance test, boolean ignoreIds) {
        TestCase.assertNotNull("Original is Null", original);
        TestCase.assertNotNull("Test is Null", test);

        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

        TestCase.assertEquals(original.getState(), test.getState());

        assertApplianceDefinitionsAreEqual(original.getIdApplianceDefinition(), test.getIdApplianceDefinition(), ignoreIds);
        assertAppliaceDeploymentsAreEqual(original.getIdApplianceDeployment(), test.getIdApplianceDeployment(), ignoreIds);

        logger.info("Appliances are equivalent");

    }

    protected void assertAppliaceDeploymentsAreEqual(IdentityApplianceDeployment original, IdentityApplianceDeployment test, boolean ignoreIds) {
        TestCase.assertTrue((original == null && test == null) || (original != null && test != null));
        if (original == null)
            return;

        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

        TestCase.assertEquals(original.getId(), test.getId());
        TestCase.assertEquals(original.getDeployedRevision(), test.getDeployedRevision());
        TestCase.assertEquals(original.getDeploymentTime(), test.getDeploymentTime());
        TestCase.assertEquals(original.getDescription(), test.getDescription());
        TestCase.assertEquals(original.getFeatureName(), test.getFeatureName());
        TestCase.assertEquals(original.getFeatureUri(), test.getFeatureUri());
        TestCase.assertEquals(original.getState(), test.getState());

        // TODO IDAUs

    }

    protected void assertApplianceDefinitionsAreEqual(IdentityApplianceDefinition original, IdentityApplianceDefinition test, boolean ignoreIds) {

        logger.debug("Original:" + (original == null ? "<NULL>" : original.toString()));
        logger.debug("Test    :" + (test == null ? "<NULL>" : test.toString()));
        
        TestCase.assertTrue((original == null && test == null) || (original != null && test != null));
        if (original == null || original == test)
            return;

        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

        TestCase.assertEquals( original.getName(), test.getName()) ;
        TestCase.assertEquals( original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString()) ;
        TestCase.assertEquals( original.getProviders().size() , test.getProviders().size());

        for (Provider originalProvider : original.getProviders()) {
            boolean found = false;
            for (Provider testProvider : test.getProviders()) {
                if (originalProvider.getName().equals(testProvider.getName())) {
                    assertProvidersAreEqual(originalProvider, testProvider, ignoreIds);
                    found = true;
                }
            }
            assert found : "Provider " + originalProvider.getName() + " not found";
        }

    }


    protected void assertProvidersAreEqual(Provider original, Provider test, boolean ignoreIds) {

        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

        TestCase.assertEquals(original.getName(), test.getName());
        TestCase.assertEquals(original.getDescription(), test.getDescription());
        TestCase.assertEquals(original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        TestCase.assertEquals(original.getClass().getName(), test.getClass().getName());


        Provider originalL = (Provider) original;
        Provider testL = (Provider) test;
        assertConfigsAreEqual(originalL.getConfig(), testL.getConfig(), ignoreIds);

        if (originalL instanceof ServiceProvider) {

            ServiceProvider originalSp = (ServiceProvider) originalL;
            ServiceProvider testSp = (ServiceProvider) testL;

            assertIdenityLookupsAreEqual(originalSp.getIdentityLookup(), testSp.getIdentityLookup(), ignoreIds);
            assertActivationsAreEqual(originalSp.getActivation(), testSp.getActivation(), ignoreIds);

            if (originalSp.getFederatedConnectionsA() != null) {
                TestCase.assertEquals(originalSp.getFederatedConnectionsA().size(), testSp.getFederatedConnectionsA().size());

                for (FederatedConnection originalC : originalSp.getFederatedConnectionsA()) {

                    boolean found = false;
                    for (FederatedConnection testC : testSp.getFederatedConnectionsA()) {
                        if (!ignoreIds && originalC.getId() == testC.getId()) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        } else if (originalC.getName().equals(testC.getName())) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        }
                    }

                    TestCase.assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }

            if (originalSp.getFederatedConnectionsB() != null) {

                TestCase.assertEquals(originalSp.getFederatedConnectionsB().size(), testSp.getFederatedConnectionsB().size());

                for (FederatedConnection originalC : originalSp.getFederatedConnectionsB()) {

                    boolean found = false;
                    for (FederatedConnection testC : testSp.getFederatedConnectionsB()) {

                        if (!ignoreIds && originalC.getId() == testC.getId()) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        } else if (originalC.getName().equals(testC.getName())) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        }
                    }

                    TestCase.assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }


        } else if (originalL instanceof IdentityProvider) {
            IdentityProvider originalIdp = (IdentityProvider) originalL;
            IdentityProvider testIdp = (IdentityProvider) testL;

            assertIdenityLookupsAreEqual(originalIdp.getIdentityLookup(), testIdp.getIdentityLookup(), ignoreIds);

            if (originalIdp.getFederatedConnectionsA() != null) {

                TestCase.assertEquals(originalIdp.getFederatedConnectionsA().size(), testIdp.getFederatedConnectionsA().size());

                for (FederatedConnection originalC : originalIdp.getFederatedConnectionsA()) {

                    boolean found = false;
                    for (FederatedConnection testC : testIdp.getFederatedConnectionsA()) {

                        if (!ignoreIds && originalC.getId() == testC.getId()) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        } else if (originalC.getName().equals(testC.getName())) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        }
                    }

                    TestCase.assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }

            if (originalIdp.getFederatedConnectionsB() != null) {

                TestCase.assertEquals(originalIdp.getFederatedConnectionsB().size(), testIdp.getFederatedConnectionsB().size());

                for (FederatedConnection originalC : originalIdp.getFederatedConnectionsB()) {

                    boolean found = false;
                    for (FederatedConnection testC : testIdp.getFederatedConnectionsB()) {
                        if (!ignoreIds && originalC.getId() == testC.getId()) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        } else if (originalC.getName().equals(testC.getName())) {
                            found = true;
                            assertFederatedConnectionsAreEqual(originalC, testC, ignoreIds);
                            break;
                        }
                    }

                    TestCase.assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }


        } else if (originalL instanceof ProvisioningServiceProvider) {
            // TODO :
        }
    }

    protected void assertIdenityLookupsAreEqual(IdentityLookup original, IdentityLookup test, boolean ignoreIds) {
        // TODO :

        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

    }

    protected void assertActivationsAreEqual(Activation original, Activation test, boolean ignoreIds) {
        // TODO :
        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

    }

    protected void assertFederatedConnectionsAreEqual(FederatedConnection original, FederatedConnection test, boolean ignoreIds) {
        // TODO :
        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

    }


    protected void assertChannelsAreEqual(Channel original, Channel test, boolean ignoreIds) {

        TestCase.assertEquals( original.getName(), test.getName());
        TestCase.assertEquals( original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        TestCase.assertEquals( original.getClass().getName(), test.getClass().getName());
        TestCase.assertEquals( original.isOverrideProviderSetup(), test.isOverrideProviderSetup());

        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

        if (original instanceof IdentityProviderChannel) {
            IdentityProviderChannel originalIdP = (IdentityProviderChannel) original;
            originalIdP.getAccountLinkagePolicy(); // TODO
            // TODO : assertIdentityVaultsAreEqual(originalIdP.getIdentityVault(), ((IdentityProviderChannel)test).getIdentityVault(), ignoreIds);
        } else if (original instanceof ServiceProviderChannel) {
            ServiceProviderChannel originalSP = (ServiceProviderChannel) original;
            originalSP.getEmissionPolicy(); // TODO
        }

    }

    private void assertIdentityVaultsAreEqual(IdentitySource original, IdentitySource test, boolean ignoreIds) {
        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());


        DbIdentitySource dbOriginal = (DbIdentitySource) original;
        DbIdentitySource dbTest = (DbIdentitySource) test;

        TestCase.assertEquals(dbOriginal.getName(), dbTest.getName());
        TestCase.assertEquals(dbOriginal.getAdmin(), dbTest.getAdmin());
        TestCase.assertEquals(dbOriginal.getPassword(), dbTest.getPassword());
        TestCase.assertEquals(dbOriginal.getConnectionUrl(), dbTest.getConnectionUrl());

        // TODO : Queries
    }

    protected void assertConfigsAreEqual(ProviderConfig original, ProviderConfig test, boolean ignoreIds) {
        // TODO :
        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

    }

    protected void assertResourcesAreEqual(Resource original, Resource test, boolean ignoreIds) {
        // TODO :
        if (!ignoreIds)
            TestCase.assertEquals(original.getId(), test.getId());

    }

    protected IdentityAppliance newApplianceInstance(String name) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("/com/atricore/idbus/console/lifecycle/main/test/appliance-model-beans.xml");
        return (IdentityAppliance) ctx.getBean(name);
    }

}
