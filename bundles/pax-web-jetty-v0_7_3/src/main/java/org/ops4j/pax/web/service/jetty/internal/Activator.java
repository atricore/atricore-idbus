/*
 * Copyright 2009 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.web.service.jetty.internal;

import java.util.Hashtable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.web.service.spi.ServerControllerFactory;

/**
 * @author Alin Dreghiciu (adreghiciu@gmail.com)
 * @since 0.7.0, July 31, 2009
 */
public class Activator
    implements BundleActivator
{

    private static final Log logger = LogFactory.getLog(Activator.class);

    public Activator()
    {

        logger.debug("Using Atricore Jetty Bundle Activator");

        final ClassLoader backup = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader( Activator.class.getClassLoader() );
        JCLLogger.init();
        Thread.currentThread().setContextClassLoader( backup );
    }

    public void start( BundleContext bundleContext )
        throws Exception
    {

        /* switch to spring-dm
        bundleContext.registerService(
            ServerControllerFactory.class.getName(),
            new ConfigurableServerControllerFactoryImpl(),
            new Hashtable()
        ); */
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        // TODO unregister
    }

}
