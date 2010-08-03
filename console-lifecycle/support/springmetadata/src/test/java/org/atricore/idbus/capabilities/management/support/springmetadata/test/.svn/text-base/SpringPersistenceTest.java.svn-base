/*
 * Copyright (c) 2010., Atricore Inc.
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
import org.apache.derby.drda.NetworkServerControl;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Alias;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Bean;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Beans;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.BeansDefinition;
import org.atricore.idbus.capabilities.management.support.springmetadata.test.util.AbstractDBServerTest;
import org.atricore.idbus.capabilities.management.support.springmetadata.util.BeanUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SpringPersistenceTest extends AbstractDBServerTest {

    private static final Log logger = LogFactory.getLog(SpringPersistenceTest.class.getName());

    private static final String BEANS_DEFINITION_1 = "/org/atricore/idbus/capabilities/management/support/springmetadata/test/test-beans-1.xml";

    private static final String BEANS_DEFINITION_2 = "/org/atricore/idbus/capabilities/management/support/springmetadata/test/test-beans-2.xml";

    private static final String BEANS_DEFINITION_3 = "/org/atricore/idbus/capabilities/management/support/springmetadata/test/test-beans-3.xml";


    @Test
    public void unmarshallAppContextDescriptor() throws Exception {

        InputStream is = getClass().getResourceAsStream(BEANS_DEFINITION_1);
        Beans b = BeanUtils.unmarshal(is);
        List<String> descrs =  b.getDescription().getContent();

        for (String descr : descrs) {
            logger.info("Description: " + descr);
        }

    }


    // BEANS Create Retrieve Update Delete tests

    /**
     * Beans Create tests (Crud)
     *
     * @throws Exception
     */
    @Test
    public void createBeans() throws Exception {

        // Persistence of a Product and a Book.
        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            logger.debug("Persisting Beans");

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION_1));
            BeansDefinition beansDefinition = new BeansDefinition ();

            beansDefinition.setName("MyBeansDefinitionSet1");
            beansDefinition.setBeans(beans);

            StringBuffer description = new StringBuffer();
            if (beans.getDescription() != null) {
                for (String d : beans.getDescription().getContent()) {
                    description.append(d);
                }
            }

            logger.debug("Beans.description.content(concat)" + description);
            logger.debug("About to persist Beans");

            pm.makePersistent(beansDefinition);

            tx.commit();
            logger.debug("Beans have been persisted");
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
            pm.close();
        }


    }

    /**
     * Beans Retrieve tests (cRud)
     *
     * @throws Exception
     */
    @Test
    public void retrieveBeans() throws Exception {
        // Persistence of a Product and a Book.
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            String name = "MyBeansDefinitionSet1";

            logger.debug("Retrieving BeansDefinitino " + name);
            Extent e = pm.getExtent(BeansDefinition.class, true);
            Query q = pm.newQuery(e, "name == '"+name+"'");
            Collection c = (Collection) q.execute();

            assert c != null : "Null result from query execution, expected Collection";
            assert c.size() == 1 : "Too many/few bean definitions found, exepted 1, received " + c.size();

            BeansDefinition beansDefinition = (BeansDefinition) c.iterator().next();

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION_1));
            assertEquivalent(beansDefinition.getBeans(), beans);

            BeanUtils.marshal(beansDefinition.getBeans());

            tx.commit();
            logger.debug("Bean has been retrieved");
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
            pm.close();
        }
    }

    /**
     * Beans Delete tests (cruD)
     *
     * @throws Exception
     */
    @Test
    public void deleteBeans() throws Exception {
        // Persistence of a Product and a Book.
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();
            String name = "MyBeansDefinitionSet1";

            logger.debug("Retrieving BeansDefinitino " + name);
            Extent e = pm.getExtent(BeansDefinition.class, true);
            Query q = pm.newQuery(e, "name == '"+name+"'");
            Collection c = (Collection) q.execute();

            assert c != null : "Null result from query execution, expected Collection";
            assert c.size() == 1 : "Too many/few bean definitions found, exepted 1, received " + c.size();

            BeansDefinition beansDefinition = (BeansDefinition) c.iterator().next();

            List<Object> toDelete = new ArrayList<Object>();

            // TODO : Extend this pattern to other relations in the model
            for (Object o : beansDefinition.getBeans().getImportsAndAliasAndBeen()) {
                if (o instanceof Bean) {
                    Bean b = (Bean) o;
                    for (Object o1 : b.getMetasAndConstructorArgsAndProperties()) {
                        toDelete.add(o1);
                    }

                    b.getMetasAndConstructorArgsAndProperties().clear();

                }
                toDelete.add(o);
            }

            beansDefinition.getBeans().getImportsAndAliasAndBeen().clear();

            pm.deletePersistent(beansDefinition);

            for (Object o : toDelete) {
                pm.deletePersistent(o);
            }

            tx.commit();
            logger.debug("Bean has been persisted");
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
            pm.close();
        }

    }

    @Test
    public void persistBeansAdvanced() throws Exception {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION_2));

            BeansDefinition bd = new BeansDefinition();
            bd.setName(BEANS_DEFINITION_2);
            bd.setBeans(beans);

            pm.makePersistent(bd);

            tx.commit();

        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
        }
    }

    @Test
    public void retrieveBeansAdvanced() throws Exception {
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();

            logger.debug("Retrieving BeansDefinitino " + BEANS_DEFINITION_2);
            Extent e = pm.getExtent(BeansDefinition.class, true);
            Query q = pm.newQuery(e, "name == '"+BEANS_DEFINITION_2+"'");
            Collection c = (Collection) q.execute();

            assert c != null : "Null result from query execution, expected Collection";
            assert c.size() == 1 : "Too many/few bean definitions found, exepted 1, received " + c.size();

            BeansDefinition beansDefinition = (BeansDefinition) c.iterator().next();

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION_2));
            assertEquivalent(beansDefinition.getBeans(), beans);

            BeanUtils.marshal(beansDefinition.getBeans());

            tx.commit();
            logger.debug("Bean has been retrieved");
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
        }

    }

    @Test
    public void persistBeansFull() throws Exception {

        PersistenceManager pm = pmf.getPersistenceManager();

        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION_3));

            BeansDefinition bd = new BeansDefinition();
            bd.setName(BEANS_DEFINITION_3);
            bd.setBeans(beans);

            pm.makePersistent(bd);

            tx.commit();

        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
        }
    }

    @Test
    public void retrieveBeansFull() throws Exception {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            logger.debug("Retrieving BeansDefinitino " + BEANS_DEFINITION_3);
            Extent e = pm.getExtent(BeansDefinition.class, true);
            Query q = pm.newQuery(e, "name == '"+BEANS_DEFINITION_3+"'");
            Collection c = (Collection) q.execute();

            assert c != null : "Null result from query execution, expected Collection";
            assert c.size() == 1 : "Too many/few bean definitions found, exepted 1, received " + c.size();

            BeansDefinition beansDefinition = (BeansDefinition) c.iterator().next();

            Beans beans = BeanUtils.unmarshal(getClass().getResourceAsStream(BEANS_DEFINITION_3));
            assertEquivalent(beansDefinition.getBeans(), beans);

            BeanUtils.marshal(beansDefinition.getBeans());

            tx.commit();
            logger.debug("Bean has been retrieved");
        } finally {
            if (tx.isActive()) {
                logger.warn("Rollback transaction!");
                tx.rollback();
            }
        }


    }
    


}
