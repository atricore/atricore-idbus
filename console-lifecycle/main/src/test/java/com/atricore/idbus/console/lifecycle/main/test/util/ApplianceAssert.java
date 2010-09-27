package com.atricore.idbus.console.lifecycle.main.test.util;

import com.atricore.idbus.console.lifecycle.main.domain.IdentityAppliance;
import com.atricore.idbus.console.lifecycle.main.domain.IdentityApplianceDeployment;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplianceAssert {

    private static final Log logger = LogFactory.getLog(ApplianceAssert.class);

    public static void assertAppliancesAreEqual(IdentityAppliance original, IdentityAppliance test, boolean ignoreIds) {
        assertNotNull("Original is Null", original);
        assertNotNull("Test is Null", test);

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        assertEquals(original.getState(), test.getState());

        assertApplianceDefinitionsAreEqual(original.getIdApplianceDefinition(), test.getIdApplianceDefinition(), ignoreIds);
        assertAppliaceDeploymentsAreEqual(original.getIdApplianceDeployment(), test.getIdApplianceDeployment(), ignoreIds);

        logger.info("Appliances are equivalent");

    }

    public static void assertAppliaceDeploymentsAreEqual(IdentityApplianceDeployment original, IdentityApplianceDeployment test, boolean ignoreIds) {
        assertTrue((original == null && test == null) || (original != null && test != null));
        if (original == null)
            return;

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        assertEquals(original.getId(), test.getId());
        assertEquals(original.getDeployedRevision(), test.getDeployedRevision());
        assertEquals(original.getDeploymentTime(), test.getDeploymentTime());
        assertEquals(original.getDescription(), test.getDescription());
        assertEquals(original.getFeatureName(), test.getFeatureName());
        assertEquals(original.getFeatureUri(), test.getFeatureUri());
        assertEquals(original.getState(), test.getState());

        // TODO IDAUs

    }

    public static void assertApplianceDefinitionsAreEqual(IdentityApplianceDefinition original, IdentityApplianceDefinition test, boolean ignoreIds) {

        logger.debug("Original:" + (original == null ? "<NULL>" : original.toString()));
        logger.debug("Test    :" + (test == null ? "<NULL>" : test.toString()));

        assertTrue((original == null && test == null) || (original != null && test != null));
        if (original == null || original == test)
            return;

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        assertEquals(original.getName(), test.getName()) ;
        assertEquals(original.getDisplayName(), test.getDisplayName());
        assertEquals(original.getDescription(), test.getDescription());
        assertEquals(original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString()) ;
        assertEquals(original.getProviders().size() , test.getProviders().size());


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

        assertEquals(original.getExecutionEnvironments().size(), test.getExecutionEnvironments().size());
        for (ExecutionEnvironment originalExecEnv : original.getExecutionEnvironments()) {
            boolean found = false;
            for (ExecutionEnvironment testExecEnv : test.getExecutionEnvironments()) {
                if (originalExecEnv.getName().equals(testExecEnv.getName())) {
                    assertExecEnvsAreEqual(originalExecEnv, testExecEnv, ignoreIds);
                    found = true;

                }
            }
            assert found : "Exec.Env. " + originalExecEnv.getName() + " not found";
        }

        assertEquals(original.getIdentitySources().size(), test.getIdentitySources().size());
        for (IdentitySource originalIdSource : original.getIdentitySources()) {
            boolean found = false;
            for (IdentitySource testIdSource : test.getIdentitySources()) {
                if (originalIdSource.getName().equals(testIdSource.getName())) {
                    assertIdentitySourcesAreEqual(originalIdSource, testIdSource, ignoreIds);
                    found = true;
                }
            }
        }

    }


    public static void assertProvidersAreEqual(Provider original, Provider test, boolean ignoreIds) {

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        assertEquals(original.getName(), test.getName());
        assertEquals(original.getDisplayName(), test.getDisplayName());
        assertEquals(original.getDescription(), test.getDescription());
        assertEquals(original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());
        assertEquals(original.getClass().getName(), test.getClass().getName());


        Provider originalL = (Provider) original;
        Provider testL = (Provider) test;
        assertConfigsAreEqual(originalL.getConfig(), testL.getConfig(), ignoreIds);

        if (originalL instanceof ServiceProvider) {

            ServiceProvider originalSp = (ServiceProvider) originalL;
            ServiceProvider testSp = (ServiceProvider) testL;

            assertIdenityLookupsAreEqual(originalSp.getIdentityLookup(), testSp.getIdentityLookup(), ignoreIds);
            assertActivationsAreEqual(originalSp.getActivation(), testSp.getActivation(), ignoreIds);

            if (originalSp.getFederatedConnectionsA() != null) {
                assertEquals(originalSp.getFederatedConnectionsA().size(), testSp.getFederatedConnectionsA().size());

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

                    assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }

            if (originalSp.getFederatedConnectionsB() != null) {

                assertEquals(originalSp.getFederatedConnectionsB().size(), testSp.getFederatedConnectionsB().size());

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

                    assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }


        } else if (originalL instanceof IdentityProvider) {
            IdentityProvider originalIdp = (IdentityProvider) originalL;
            IdentityProvider testIdp = (IdentityProvider) testL;

            assertIdenityLookupsAreEqual(originalIdp.getIdentityLookup(), testIdp.getIdentityLookup(), ignoreIds);

            if (originalIdp.getFederatedConnectionsA() != null) {

                assertEquals(originalIdp.getFederatedConnectionsA().size(), testIdp.getFederatedConnectionsA().size());

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

                    assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }

            if (originalIdp.getFederatedConnectionsB() != null) {

                assertEquals(originalIdp.getFederatedConnectionsB().size(), testIdp.getFederatedConnectionsB().size());

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

                    assertTrue("FederatedConnection " + originalC.getName() + " not found.", found);
                }
            }


        } else if (originalL instanceof ProvisioningServiceProvider) {
            // TODO :
        }
    }

    public static void assertIdenityLookupsAreEqual(IdentityLookup original, IdentityLookup test, boolean ignoreIds) {

        assertEquals(original.getName(), test.getName());
        assertEquals(original.getDisplayName(), test.getDisplayName());
        assertEquals(original.getDescription(), test.getDescription());

        assertEquals(original.getIdentitySource().getName(), test.getIdentitySource().getName());
        assertEquals(original.getProvider().getName(), test.getProvider().getName());

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

    }

    public static void assertActivationsAreEqual(Activation original, Activation test, boolean ignoreIds) {
        assertEquals(original.getName(), test.getName());
        assertEquals(original.getDisplayName(), test.getDisplayName());
        assertEquals(original.getDescription(), test.getDescription());
        assertEquals(original.getExecutionEnv().getName(), test.getExecutionEnv().getName());

        if (original instanceof JOSSOActivation) {
            JOSSOActivation jao = (JOSSOActivation) original;
            JOSSOActivation jat = (JOSSOActivation) test;

            assertEquals(jao.getPartnerAppId(), jat.getPartnerAppId());
            assertEquals(jao.getPartnerAppLocation().getLocationAsString(), jat.getPartnerAppLocation().getLocationAsString());

        }

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

    }

    public static void assertFederatedConnectionsAreEqual(FederatedConnection original, FederatedConnection test, boolean ignoreIds) {
        assertEquals(original.getName(), test.getName());
        assertChannelsAreEqual(original.getChannelA(), test.getChannelA(), ignoreIds);
        assertChannelsAreEqual(original.getChannelB(), test.getChannelB(), ignoreIds);
        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

    }


    public static void assertChannelsAreEqual(Channel original, Channel test, boolean ignoreIds) {

        assertEquals( original.getName(), test.getName());

        if (original.getLocation() != null)
            assertEquals( original.getLocation().getLocationAsString(), test.getLocation().getLocationAsString());

        assertEquals( original.getClass().getName(), test.getClass().getName());
        assertEquals( original.isOverrideProviderSetup(), test.isOverrideProviderSetup());

        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        if (original instanceof IdentityProviderChannel) {
            IdentityProviderChannel originalIdP = (IdentityProviderChannel) original;
            IdentityProviderChannel testIdP = (IdentityProviderChannel) test;

            assertEquals( originalIdP.isPreferred(), testIdP.isPreferred());

            originalIdP.getAccountLinkagePolicy(); // TODO
        } else if (original instanceof ServiceProviderChannel) {
            ServiceProviderChannel originalSP = (ServiceProviderChannel) original;
            originalSP.getEmissionPolicy(); // TODO

        }

    }

    public static void assertExecEnvsAreEqual(ExecutionEnvironment original, ExecutionEnvironment test, boolean ignoreIds) {
        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        assertEquals(original.getName(), test.getName());
        assertEquals(original.getInstallUri(), test.getInstallUri());
        assertEquals(original.getActive(), test.getActive());
        assertEquals(original.getDescription(), test.getDescription());
        assertEquals(original.getDisplayName(), test.getDisplayName());
        assertEquals(original.getPlatformId(), test.getPlatformId());
        assertEquals(original.isInstallDemoApps(), test.isInstallDemoApps());
        assertEquals(original.isOverwriteOriginalSetup(), test.isOverwriteOriginalSetup());

        // TODO : Specific envs ?

    }

    public static void assertIdentitySourcesAreEqual(IdentitySource original, IdentitySource test, boolean ignoreIds) {
        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

        assertEquals(original.getName(), test.getName());
        assertEquals(original.getDisplayName(), test.getDisplayName());
        assertEquals(original.getDescription(), test.getDescription());

        if (original instanceof DbIdentitySource) {
            DbIdentitySource dbOriginal = (DbIdentitySource) original;
            DbIdentitySource dbTest = (DbIdentitySource) test;

            assertEquals(dbOriginal.getAdmin(), dbTest.getAdmin());
            assertEquals(dbOriginal.getPassword(), dbTest.getPassword());
            assertEquals(dbOriginal.getConnectionUrl(), dbTest.getConnectionUrl());

            // TODO : Queries

        } else if (original instanceof EmbeddedIdentitySource) {
            EmbeddedIdentitySource ebOriginal = (EmbeddedIdentitySource) original;
            EmbeddedIdentitySource ebTest = (EmbeddedIdentitySource) test;

            assertEquals(ebOriginal.getIdau(), ebTest.getIdau());
            assertEquals(ebOriginal.getPsp(), ebTest.getPsp());
            assertEquals(ebOriginal.getPspTarget(), ebTest.getPspTarget());
        }


    }

    public static void assertConfigsAreEqual(ProviderConfig original, ProviderConfig test, boolean ignoreIds) {
        // TODO :
        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

    }

    public static void assertResourcesAreEqual(Resource original, Resource test, boolean ignoreIds) {
        // TODO :
        if (!ignoreIds)
            assertEquals(original.getId(), test.getId());

    }


}
