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

package com.atricore.idbus.console.lifecycle.support.springmetadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atricore.idbus.console.lifecycle.support.springmetadata.model.*;
import com.atricore.idbus.console.lifecycle.support.springmetadata.util.BeanUtils;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.jdo.*;

/**
 * Transaction and Persistence Manager Factory lifecycle should be managed by the application.
 *
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public class JDOXmlApplicationContext extends AbstractXmlApplicationContext {

    private static final Log logger = LogFactory.getLog(JDOXmlApplicationContext.class);

    private String beansDefinitionName;
    private Resource[] configResources;

    public JDOXmlApplicationContext(PersistenceManagerFactory pmf) {
        super();
        this.configResources = loadJDOResources(pmf);
        refresh();
    }

    public JDOXmlApplicationContext(PersistenceManagerFactory pmf, String beansDefinitionName) {
        super();


        this.beansDefinitionName = beansDefinitionName;
        this.configResources = loadJDOResources(pmf, beansDefinitionName);
        refresh();
    }


    protected Resource[] getConfigResources() {
        return this.configResources;
    }

    protected Resource[] loadJDOResources(PersistenceManagerFactory pmf, String beansDefinitionName) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();

        try {

            if (logger.isDebugEnabled())
                logger.debug("Retrieving Extent for Spring Beans : " + beansDefinitionName);

            // Do we have to manage our own transaction ?

            if (!tx.isActive()) {
                if (logger.isDebugEnabled())
                    logger.debug("Starting transaction");
                tx.begin();
            } else {
                tx = null;
                if (logger.isDebugEnabled())
                    logger.debug("Using transaction already active");

            }

            Extent e = pm.getExtent(BeansDefinition.class, true);
            Query q = pm.newQuery(e, "name == '" + beansDefinitionName + "'");
            Collection c = (Collection) q.execute();

            assert c != null : "Null result from query execution, expected Collection";
            assert c.size() == 1 : "Too many/few bean definitions found, exepted 1, received " + c.size();

            BeansDefinition beansDef = (BeansDefinition) c.iterator().next();
            String strBeansDef = BeanUtils.marshal(beansDef.getBeans());

            if (logger.isTraceEnabled())
                logger.trace("Beans definition:---------------\n" + strBeansDef);

            if (tx != null) {
                if (logger.isDebugEnabled())
                    logger.debug("Commit transaction");
                tx.commit();
            }

            return new Resource[] { new ByteArrayResource(strBeansDef.getBytes()) };

        } catch (Exception e) {
            logger.error("Error loading JDO Spring Resource : " + e.toString(), e);
        } finally {
            if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active, rollback!");
                tx.rollback();
            }
        }

        return new Resource[0];

    }

    protected Resource[] loadJDOResources(PersistenceManagerFactory pmf) {

        PersistenceManager pm = pmf.getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        List<Resource> resources = new ArrayList<Resource>();

        try {

            if (logger.isDebugEnabled())
                logger.debug("Retrieving Extent for Spring Beans");


            // Do we have to manage our own transaction ?

            if (!tx.isActive()) {
                if (logger.isDebugEnabled())
                    logger.debug("Starting transaction");
                tx.begin();
            } else {
                tx = null;
                if (logger.isDebugEnabled())
                    logger.debug("Using already active");

            }

            Extent e = pm.getExtent(BeansDefinition.class, true);

            for (Object o : e) {

                BeansDefinition beansDef = (BeansDefinition) o;
                logger.debug("Adding resource for beans definition " + beansDef.getName());

                String strBeansDef = BeanUtils.marshal(beansDef.getBeans());
                if (logger.isTraceEnabled())
                    logger.trace("Beans definition:\n" + strBeansDef);

                resources.add(new ByteArrayResource(strBeansDef.getBytes()));

            }

            if (tx != null) tx.commit();

        } catch (Exception e) {
            logger.error("Error loading JDO Spring Resource : " + e.toString(), e);
        } finally {
            if (tx != null && tx.isActive()) {
                logger.error("Transaction is still active, rollback!");
                tx.rollback();
            }
        }

        return resources.toArray(new Resource[resources.size()]);
    }



}
