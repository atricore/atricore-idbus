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

package org.atricore.idbus.capabilities.management.support.springmetadata.test.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.drda.NetworkServerControl;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Bean;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.Beans;
import org.atricore.idbus.capabilities.management.support.springmetadata.model.BeansDefinition;
import org.atricore.idbus.capabilities.management.support.springmetadata.util.BeanUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public abstract class AbstractDBServerTest {

    private static final Log logger = LogFactory.getLog(AbstractDBServerTest.class);

    protected static PersistenceManagerFactory pmf;

    protected static NetworkServerControl derbyServer;

    @BeforeClass
    public static void setupClass() throws Exception {

        InetAddress address = InetAddress.getByName("localhost");

        // TODO : Extract to spring app. context
        System.setProperty("derby.system.home", "./target/derby/");
        derbyServer = new NetworkServerControl(address,
                1537,
                "atricore",
                "ádmin");

        derbyServer.start(new PrintWriter(System.out));

        pmf = JDOHelper.getPersistenceManagerFactory("datanucleus-tests.properties");

    }

    @AfterClass
    public static void tearDownClass() throws Exception{
        derbyServer.shutdown();
    }

    protected void assertEquivalent(BeansDefinition b1, BeansDefinition b2) {
        assert b1.getName().equals(b2.getName()) : "BeanDefinitions name do not match [" + b1.getName() + "/" + b2.getName() + "]";

        // TODO : Other attribues ...
        assertEquivalent(b1.getBeans(), b2.getBeans());

    }

    protected void assertEquivalent(Beans b1, Beans b2) {
        assert b1.getImportsAndAliasAndBeen().size() == b2.getImportsAndAliasAndBeen().size();

        // TODO : Add more conditions?

        // Will JDO respect the original order in the list ?
    }


    protected void assertEquivalent(Bean b1, Bean b2) {
        assert b1.getName().equals(b2.getName());
        assert b1.getClazz().equals(b2.getClazz());
        // TODO : more
    }


    protected static BeansDefinition importBeans(String beansDefinitionResource) throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(beansDefinitionResource);
        assert is != null  : "Beans resource not found " + beansDefinitionResource;

        Beans beans = BeanUtils.unmarshal(is);
        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {
            tx.begin();

            BeansDefinition def = new BeansDefinition();
            def.setName(beansDefinitionResource);
            def.setBeans(beans);

            pm.makePersistent(def);

            tx.commit();

            return def;
        } finally {
            if (tx.isActive()) {
                logger.warn("Transaction rollback!");
                tx.rollback();
            }
        }

    }

    
}
