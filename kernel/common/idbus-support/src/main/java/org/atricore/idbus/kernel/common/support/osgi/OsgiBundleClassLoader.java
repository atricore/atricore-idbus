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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.osgi.framework.Bundle;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiBundleClassLoader extends ClassLoader {

    private final Bundle bundle;

    public OsgiBundleClassLoader(Bundle bundle, ClassLoader parent) {
        super(parent);
        this.bundle = bundle;
    }


    public OsgiBundleClassLoader(Bundle bundle) {
        super();
        this.bundle = bundle;
    }

    protected Bundle getBundle() {
        return bundle;
    }

    @Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			return getBundle().loadClass(name);
		} catch (ClassNotFoundException ex) {
            return super.findClass(name);
		}
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = findClass(name);
		if (resolve)
			resolveClass(clazz);
		return clazz;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return loadClass(name, false);
	}

	@Override
	protected URL findResource(String name) {
		return getBundle().getEntry("/" + name);
	}

	@Override
	public URL getResource(String name) {
		return findResource(name);
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		Vector<URL> resources = new Vector<URL>();
		URL resource = getResource(name);
		if (resource != null) {
			resources.add(resource);
		}
		return resources.elements();
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		return findResources(name);
	}

	@Override
	public String toString() {
		return super.toString() + String.format("[bundle-symbolic-name = '%s']", getBundle().getSymbolicName());
	}


}
