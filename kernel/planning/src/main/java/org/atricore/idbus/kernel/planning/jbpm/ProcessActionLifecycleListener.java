/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
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

package org.atricore.idbus.kernel.planning.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.osgi.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Bundle;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;

import java.util.Map;

/**
 * Convinience class to registration process actions in an OSGi environment
 * 
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 212 $ $Date: 2008-12-05 14:53:51 -0300 (Fri, 05 Dec 2008) $
 */
public class ProcessActionLifecycleListener implements BundleContextAware {

    protected final transient Log logger = LogFactory.getLog(getClass());

    private BundleContext bundleContext;

    private ProcessRegistryImpl registry;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void onBind(ProcessAction processAction, Map properties) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Process Action [" + processAction.getQualifiedClassName() + "] Registered");

        String bundleSymbolicName = (String) properties.get("Bundle-SymbolicName");
        String bundleVersion = (String) properties.get("Bundle-Version");

        if (logger.isDebugEnabled())
            logger.debug("Registering action from action contributor bundle " +
                    bundleSymbolicName + ":" + bundleVersion);

        boolean found = false;

        for (Bundle b : bundleContext.getBundles()) {

            if (logger.isTraceEnabled())
                logger.trace("Checking bundle " + b.getSymbolicName() + "/" + b.getVersion());

            if (bundleSymbolicName.equals(b.getSymbolicName()) &&
                bundleVersion.equals(b.getVersion().toString())) {

                // Add this bundle to the registry!
                found = true;
                registry.registerAction(processAction, b);
            }

        }
        if (!found) {
            throw new IdentityPlanningException("Bundle not found in context ["+
                    bundleSymbolicName + ":" + bundleVersion + "], can't registration process action " +
                    processAction.getQualifiedClassName());
        }
    }

    public void onUnbind(ProcessAction processAction, Map properties) throws Exception {
        logger.info("Process Action [" + processAction.getQualifiedClassName() + "] Unregistered");
    }

    public ProcessRegistryImpl getRegistry() {
        return registry;
    }

    public void setRegistry(ProcessRegistryImpl registry) {
        this.registry = registry;
    }
}