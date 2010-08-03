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

package org.atricore.idbus.kernel.common.support.osgi;

import java.util.*;
import java.net.URL;
import java.io.IOException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.packageadmin.PackageAdmin;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiBundlespaceClassLoader extends ClassLoader implements BundleListener {

    private static final Log logger = LogFactory.getLog(OsgiBundlespaceClassLoader.class);

    public OsgiBundlespaceClassLoader(BundleContext bundleContext,
            ClassLoader parent, Class<?>... prioritizedClasses) {
        this.bundleContext = bundleContext;
        this.supportingClassLoader = parent;

        initBundleClassLoaders(retrieveClassesBundles(prioritizedClasses));
        bundleContext.addBundleListener(this);
    }

    public OsgiBundlespaceClassLoader(BundleContext bundleContext,
            ClassLoader parent, Bundle... prioritizedBundles) {
        this.bundleContext = bundleContext;
        this.supportingClassLoader = parent;

        initBundleClassLoaders(prioritizedBundles);
        bundleContext.addBundleListener(this);
    }




    @Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		Map<Bundle, OsgiBundleClassLoader> bundleClassLoaders = getBundleClassLoaders();

		for (Bundle bundle : getBundles()) {

			if (canLoadResources(bundle)) {

				if (getPrioritizedBundles().contains(bundle) || containsClassPackage(bundle, name)) {
					try {
						return bundleClassLoaders.get(bundle).loadClass(name);
					} catch (ClassNotFoundException ex) {
						/// Continue searching in the next bundle.
					} catch (Throwable ex) {
						/// Catch any other exceptions which could be thrown by a bundle loading a class.
					}
				}
			}
		}

		if (getSupportingClassLoader() != null) {
			return getSupportingClassLoader().loadClass(name);
		} else {
			throw new ClassNotFoundException(String.format(
					"Class '%s' could not be load from %s", name, getClass().getSimpleName()));
		}
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = findClass(name);
		if (resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	@Override
	protected URL findResource(String name) {

        if (logger.isDebugEnabled())
            logger.debug("findResource (by name) " + name);

		Map<Bundle, OsgiBundleClassLoader> bundleClassLoaders = getBundleClassLoaders();
		for (Bundle bundle : getBundles()) {

			if (canLoadResources(bundle)) {

                if (logger.isDebugEnabled())
                    logger.debug("findResource (by name) " + name + " in bundle " + bundle.getSymbolicName());

				try {
					URL resource = bundleClassLoaders.get(bundle).getResource(name);
					if (resource != null) {

                        if (logger.isDebugEnabled())
                            logger.debug("findResource (by name) " + name + " found in bundle " +
                                    bundle.getSymbolicName() +
                                    "(" + bundle.getBundleId() + ")" +
                                    resource);

						return resource;
					}
				} catch (Throwable ex) {
					/// Catch any other exceptions which could be thrown by a bundle loading a resource.
                    if (logger.isDebugEnabled())
                        logger.debug("Error findResource(by name) for resource " + ex.getMessage(), ex);
				}
			}
		}

		if (getSupportingClassLoader() != null) {

            if (logger.isDebugEnabled())
                logger.debug("findResourc (by name) " + name + ", looking in supporting classloaders");

			return getSupportingClassLoader().getResource(name);
		} else {
			return null;
		}
	}

	@Override
	public URL getResource(String name) {
		return findResource(name);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {

        if (logger.isDebugEnabled())
            logger.debug("findResources (by name) " + name);

		Vector<URL> resources = new Vector<URL>();
		Map<Bundle, OsgiBundleClassLoader> bundleClassLoaders = getBundleClassLoaders();
		for (Bundle bundle : getBundles()) {

			if (canLoadResources(bundle)) {

                if (logger.isDebugEnabled())
                    logger.debug("findResources (by name) " + name + " in bundle " + bundle.getSymbolicName());

				try {
                    Enumeration<URL> bundleResources = bundleClassLoaders.get(bundle).getResources(name);


					if (bundleResources != null && bundleResources.hasMoreElements()) {
						while (bundleResources.hasMoreElements()) {

                            URL u = bundleResources.nextElement();

                            if (logger.isDebugEnabled())
                                logger.debug("findResources (by name) " + name + " found in bundle " +
                                        bundle.getSymbolicName() +
                                        "(" + bundle.getBundleId() + ")" +
                                        u);

							resources.add(u);
						}
					}
				}  catch (Throwable ex) {
					/// Catch any other exceptions which could be thrown by a bundle loading a resource.
				}
			}
		}

		if (resources.size() > 0) {
			return resources.elements();
		} else {
			if (getSupportingClassLoader() != null) {

                if (logger.isDebugEnabled())
                    logger.debug("findResources (by name) " + name + ", looking in supporting classloaders");

				return getSupportingClassLoader().getResources(name);
			} else {
				return new Vector<URL>().elements();
			}
		}
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return findResources(name);
	}

	//Override
	public void bundleChanged(BundleEvent bundleEvent) {
		if (bundleEvent.getType() == BundleEvent.INSTALLED) {
			addBundle(bundleEvent.getBundle());
		} else if (bundleEvent.getType() == BundleEvent.UNINSTALLED) {
			removeBundle(bundleEvent.getBundle());
		}
	}

	protected boolean canLoadResources(Bundle bundle) {
		return bundle.getState() != Bundle.UNINSTALLED;
	}

	protected boolean containsClassPackage(Bundle bundle, String className) {
		return (bundle.getEntry("/" + getPackageName(className)) != null
				|| bundle.getEntry("/" + className.replace('.', '/') + ".class") != null);
	}

	protected String getPackageName(String className) {
		int lastDotIndex = className.lastIndexOf('.');
		return lastDotIndex >= 0 ? className.substring(0, lastDotIndex).replace('.', '/') : "";
	}


	protected void initBundleClassLoaders(Bundle... priorityBundles) {
		getPrioritizedBundles().addAll(Arrays.asList(priorityBundles));
		addBundles(priorityBundles);
		addBundles(getBundleContext().getBundles());
	}

	protected void addBundles(Bundle... bundles) {
		Bundle systemBundle = null;
		for (Bundle bundle : bundles) {
			if (!getBundleClassLoaders().containsKey(bundle)) {
				/// If it's the System Bundle, then add it to the end of the list.
				if (bundle.getBundleId() != 0) {
					addBundle(bundle);
				} else {
					systemBundle = bundle;
				}
			}
		}

		if (systemBundle != null) {
			addBundle(systemBundle);
		}
	}

	protected synchronized void addBundle(Bundle bundle) {
		getBundles().add(bundle);
		getBundleClassLoaders().put(bundle, new OsgiBundleClassLoader(bundle));
	}

	protected synchronized void removeBundle(Bundle bundle) {
		getBundles().remove(bundle);
		getBundleClassLoaders().remove(bundle);
	}

	protected Bundle[] retrieveClassesBundles(Class<?>... classes) {
		PackageAdmin packageAdmin = (PackageAdmin)getBundleContext().getService(
				getBundleContext().getServiceReference(PackageAdmin.class.getName()));

		List<Bundle> priorityBundles = new ArrayList<Bundle>();
		for (Class<?> priorityClass : classes) {
			priorityBundles.add(packageAdmin.getBundle(priorityClass));
		}

		return priorityBundles.toArray(new Bundle[0]);
	}


	private final BundleContext bundleContext;
	protected BundleContext getBundleContext() {
		return bundleContext;
	}

	private final ClassLoader supportingClassLoader;
	protected ClassLoader getSupportingClassLoader() {
		return supportingClassLoader;
	}

	private final List<Bundle> bundles = new ArrayList<Bundle>();
	protected List<Bundle> getBundles() {
		return bundles;
	}

	private final List<Bundle> prioritizedBundles = new ArrayList<Bundle>();
	protected List<Bundle> getPrioritizedBundles() {
		return prioritizedBundles;
	}

	private final Map<Bundle, OsgiBundleClassLoader> bundleClassLoaders = new HashMap<Bundle, OsgiBundleClassLoader>();
	protected Map<Bundle, OsgiBundleClassLoader> getBundleClassLoaders() {
		return bundleClassLoaders;
	}

}
