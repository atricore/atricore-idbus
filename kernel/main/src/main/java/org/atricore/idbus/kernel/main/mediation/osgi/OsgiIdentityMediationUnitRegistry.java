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

package org.atricore.idbus.kernel.main.mediation.osgi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnit;
import org.atricore.idbus.kernel.main.mediation.IdentityMediationUnitRegistry;

import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiIdentityMediationUnitRegistry {

    private static final Log logger = LogFactory.getLog(OsgiIdentityMediationUnitRegistry.class);

    private IdentityMediationUnitRegistry registry;

    public OsgiIdentityMediationUnitRegistry(IdentityMediationUnitRegistry r) {
        this.registry = r;
    }

    public void register(final IdentityMediationUnit idmu, final Map<String, ?> properties) {
        logger.info("Identity Mediation Unit registered : " + idmu.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("IDMU registered " + idmu);
        }

        registry.register(idmu.getName(), idmu);
    }

    public void unregister(final IdentityMediationUnit idmu, final Map<String, ?> properties) {
        logger.info("Identity Mediation Unit unregistered : " + idmu.getName());
        if (logger.isDebugEnabled()) {
            logger.debug("IDMU unregistered " + idmu);
        }

        registry.unregister(idmu.getName());
    }
}
