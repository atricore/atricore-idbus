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

package org.atricore.idbus.capabilities.sso.ui.internal;

import org.ops4j.pax.wicket.util.DefaultWebApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * OSGi entry point for SSO's UI bundle
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 */
public class Activator implements BundleActivator {

    private DefaultWebApplicationFactory applicationFactory;

    public void start(BundleContext context) throws Exception {
        /* programmatic startup - not needed when using blueprint/spring dm
        applicationFactory =
            new DefaultWebApplicationFactory(context, SSOUIApplication.class, "sso", "sso");
        applicationFactory.registration();
        */
    }

    public void stop(BundleContext context) throws Exception {
        /*
        applicationFactory.dispose();
        */
    }

}