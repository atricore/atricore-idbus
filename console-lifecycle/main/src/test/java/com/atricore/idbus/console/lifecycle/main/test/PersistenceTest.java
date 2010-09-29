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

import com.atricore.idbus.console.lifecycle.main.spi.IdentityApplianceManagementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class PersistenceTest {

    private static final Log logger = LogFactory.getLog( PersistenceTest.class );

    private static ApplicationContext applicationContext;

    // Use spring to manage transaction (as we do in runtime, and Maven/JUnit to trigger test cases)
    private TransactionalPersistenceTest persistenceTest;

    @BeforeClass
    public static void setupTestSuite() {
        applicationContext =
                new ClassPathXmlApplicationContext("/com/atricore/idbus/console/lifecycle/main/test/persistence-test-beans.xml");

        TransactionalPersistenceTest.setupTestSuite();

    }

    @Before
    public void setupTest() {
        Map<String, IdentityApplianceManagementService> svcs = applicationContext.getBeansOfType(IdentityApplianceManagementService.class);
        assert svcs != null && svcs.size() == 1 : "Too many/few IdentityApplianceManagementService definitions found : " + svcs;

        persistenceTest = (TransactionalPersistenceTest) applicationContext.getBean("persistenceTest");
        persistenceTest.setupTest();

    }

    @AfterClass
    public static void tearDownTestSuite() {

        TransactionalPersistenceTest.tearDownTestSuite();

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
       persistenceTest.tearDownTest();
    }

    // ----------------------------------< Basic Persistence Tests >

    @Test
    public void testAddAppliance() throws Exception{
        persistenceTest.testAddAppliance();
    }

    @Test
    public void testUpdaetAppliance() throws Exception {
        persistenceTest.testUpdaetAppliance();
    }

    @Test
    public void testDeleteAppliance() throws Exception{
        persistenceTest.testDeleteAppliance();
    }

    // ------------------------------< Fine Grained Persistence tests >

    // 1. Remove SP
    @Test
    public void testRemoveSP() throws Exception {
        persistenceTest.testRemoveSP();
    }

    // ------------------------------< DAO Persistence tests >



}
