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

import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.osgi.framework.BundleContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.logging.Level;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 252 $ $Date: 2008-12-22 18:24:21 -0200 (Mon, 22 Dec 2008) $
 */
public class OsgiProcessFragmentRegistryApplicationContext extends OsgiBundleXmlApplicationContext {

    private static final String DEFAULT_PROCESS_DESCRIPTOR_FILE = "jpdl-process-descriptor.xml";
    private static final String DEFAULT_JBPM_FRAGMENT_CFG_FILE = "META-INF/jbpm/jbpm-process-fragment-default.xml";
    private static final String DEFAULT_JBPM_EXT_FRAGMENT_CFG_FILE = "classpath*:META-INF/jbpm/jbpm.fragment";

    protected final transient Log logger = LogFactory.getLog(getClass());

    private DefaultNamespaceHandlerResolver nsHandlerResolver;
    private String[] cfgFiles;
    private URL[] cfgFileURLs;

    public OsgiProcessFragmentRegistryApplicationContext(String cf, BundleContext bundleContext) {
        this(cf,  null, bundleContext);
    }
    public OsgiProcessFragmentRegistryApplicationContext(String[] cfs, BundleContext bundleContext) {
        this(cfs, null, bundleContext);
    }

    public OsgiProcessFragmentRegistryApplicationContext(URL url,BundleContext bundleContext) {
        this(url, null, bundleContext);
    }

    public OsgiProcessFragmentRegistryApplicationContext(String cf, ApplicationContext parent, BundleContext bundleContext) {
        this(new String[] {cf}, parent, bundleContext);
    }

    public OsgiProcessFragmentRegistryApplicationContext(URL url, ApplicationContext parent, BundleContext bundleContext) {
        this(new URL[] {url}, parent, bundleContext);
    }

    public OsgiProcessFragmentRegistryApplicationContext(String[] cf, ApplicationContext parent, BundleContext bundleContext) {
        super(new String[0], parent);
        cfgFiles = cf;
        setBundleContext(bundleContext);
        refresh();
    }


    public OsgiProcessFragmentRegistryApplicationContext(URL[] url, ApplicationContext parent, BundleContext bundleContext) {
        super(new String[0], parent);
        cfgFileURLs = url;
        setBundleContext(bundleContext);
        refresh();
    }


    protected Resource[] getConfigResources() {

        List<Resource> resources = new ArrayList<Resource>();

        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread
                .currentThread().getContextClassLoader());

            Collections.addAll(resources, resolver.getResources(DEFAULT_JBPM_FRAGMENT_CFG_FILE));

            Resource[] exts = resolver.getResources(DEFAULT_JBPM_EXT_FRAGMENT_CFG_FILE);
            for (Resource r : exts) {
                InputStream is = r.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String line = rd.readLine();
                while (line != null) {
                    if (!"".equals(line)) {
                        resources.add(resolver.getResource(line));
                    }
                    line = rd.readLine();
                }
                is.close();
            }

        } catch (IOException ex) {
            // ignore
        }

        if (null == cfgFiles) {
            cfgFiles = new String[] {DEFAULT_PROCESS_DESCRIPTOR_FILE};
        }

        for (String cfgFile : cfgFiles) {
            boolean found = false;
            Resource cpr = new ClassPathResource(cfgFile);
            if (!cpr.exists()) {
                try {
                    //see if it's a URL
                    URL url = new URL(cfgFile);
                    cpr = new UrlResource(url);
                    if (cpr.exists()) {
                        resources.add(cpr);
                        found = true;
                    }
                } catch (MalformedURLException e) {
                    //ignore
                }
                if (!found) {
                    //try loading it our way
                    URL url = getResource(cfgFile, this.getClass());
                    if (url != null) {
                        cpr = new UrlResource(url);
                        if (cpr.exists()) {
                            resources.add(cpr);
                            found = true;
                        }
                    }
                }
            } else {
                resources.add(cpr);
                found = true;
            }
            if (!found) {
                logger.warn("No Process Descriptor found: " + cfgFile);
            }
        }

        if (null != cfgFileURLs) {
            for (URL cfgFileURL : cfgFileURLs) {
                UrlResource ur = new UrlResource(cfgFileURL);
                if (ur.exists()) {
                    resources.add(ur);
                } else {
                    logger.warn("No Process Descriptor found: " + cfgFileURL);
                }
            }
        }

        logger.info("Creating application context with resources: " + resources);

        if (0 == resources.size()) {
            return null;
        }

        Resource[] res = new Resource[resources.size()];
        res = resources.toArray(res);
        return res;
    }

    /**
     * Load a given resource. <p/> This method will try to load the resource
     * using the following methods (in order):
     * <ul>
     * <li>From Thread.currentThread().getContextClassLoader()
     * <li>From ClassLoaderUtil.class.getClassLoader()
     * <li>callingClass.getClassLoader()
     * </ul>
     *
     * @param resourceName The name of the resource to load
     * @param callingClass The Class object of the calling object
     */
    private URL getResource(String resourceName, Class callingClass) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null && resourceName.startsWith("/")) {
            //certain classloaders need it without the leading /
            url = Thread.currentThread().getContextClassLoader()
                .getResource(resourceName.substring(1));
        }

        if (url == null) {
            ClassLoader cl = callingClass.getClassLoader();

            if (cl != null) {
                url = cl.getResource(resourceName);
            }
        }

        if (url == null) {
            url = callingClass.getResource(resourceName);
        }

        if ((url == null) && (resourceName != null) && (resourceName.charAt(0) != '/')) {
            return getResource('/' + resourceName, callingClass);
        }

        return url;
    }



}