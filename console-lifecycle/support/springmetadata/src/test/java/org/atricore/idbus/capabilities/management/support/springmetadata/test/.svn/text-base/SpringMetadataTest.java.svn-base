/*
 * Atricore IDBus
 *
 * Copyright 2009, Atricore Inc.
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

package org.atricore.idbus.capabilities.management.support.springmetadata.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.management.support.springmetadata.exception.SpringMetadataManagementException;
import org.atricore.idbus.capabilities.management.support.springmetadata.impl.SpringMetadataManagerImpl;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Beans;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.BeansDefinition;
import org.atricore.idbus.capabilities.management.support.springmetadata.spi.SpringMetadataManager;
import org.atricore.idbus.capabilities.management.support.springmetadata.test.util.AbstractDBServerTest;
import org.atricore.idbus.capabilities.management.support.springmetadata.util.BeanUtils;
import org.junit.Before;
import org.junit.Test;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

/**
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class SpringMetadataTest extends AbstractDBServerTest {

    private static final Log logger = LogFactory.getLog(SpringMetadataTest.class.getName());

    private static final String BEANS_DEFINITION = "/org/atricore/idbus/capabilities/management/support/springmetadata/test/test-jdo-appctx-2.xml";
    private SpringMetadataManager smm;
    private static final String BEANS_DEF_NAME = "MyBeansDefinitionSet1";
    
    @Before
    public void setup(){
    	smm  = new SpringMetadataManagerImpl();
    	smm.setPmf(pmf);
    }


    @Test
    public void testSaveDefinition() throws Exception {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();

            logger.debug("Persisting Beans");

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION));
            BeansDefinition beansDefinition = new BeansDefinition();

            beansDefinition.setName(BEANS_DEF_NAME);
            beansDefinition.setBeans(beans);
            
            smm.saveBeansDefinition(beansDefinition);
            tx.commit();
        }catch (SpringMetadataManagementException e){
            logger.error("Failed to save BeansDefinition", e);
            assert false : "Failed to save BeansDefinition";
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
            pm.close();
        }
    }

    @Test
    public void testRetrieveDefinition() throws Exception {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();

            logger.debug("Retrieving BeansDefinition");

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION));

            BeansDefinition beansDef = smm.findBeansDefinition(BEANS_DEF_NAME);
            assertEquivalent(beans, beansDef.getBeans());
            tx.commit();
        }catch (SpringMetadataManagementException e){
            logger.error("Failed to save BeansDefinition", e);
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
            pm.close();
        }
    }

    @Test
    public void testRemoveDefinition() throws Exception {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();

            logger.debug("Removing BeansDefinition");
            smm.removeBeansDefinition(BEANS_DEF_NAME);
            
            tx.commit();
        }catch (SpringMetadataManagementException e){
            logger.error("Failed to remove BeansDefinition", e);
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
            pm.close();
        }
    }

}
