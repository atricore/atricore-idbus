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

package org.atricore.idbus.kernel.main.mediation.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.osgi.OsgiIdentityMediationUnit;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * This is an OSGi-friendly Apache Camel-based Identity Federation Engine .
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: CamelIdentityMediationUnitContainer.java 1359 2009-07-19 16:57:57Z sgonzalez $
 *
 * @org.apache.xbean.XBean element="osgi-identity-mediation-engine"
 */
public class OsgiCamelIdentityMediationUnitContainerImpl extends CamelIdentityMediationUnitContainer
        implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(OsgiCamelIdentityMediationUnitContainerImpl.class);

    private BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public OsgiCamelIdentityMediationUnitContainerImpl() {
        super();
        if (logger.isTraceEnabled())
            logger.trace("Creating new OsgiCamelIdentityMediationUnitContainerImpl instance");
    }

    protected CamelContext createCamelContext() throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Creating new Camel Context for " + getName());

        OsgiIdentityMediationUnit unit = (OsgiIdentityMediationUnit) getUnit();

        DefaultCamelContext ctx = new OsgiDefaultCamelContext(unit.getBundleContext());
        ctx.setRegistry(createRegistry());
        return ctx;
    }

}
