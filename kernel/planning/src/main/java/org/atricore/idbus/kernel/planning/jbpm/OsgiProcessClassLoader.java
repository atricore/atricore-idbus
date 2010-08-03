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

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.instantiation.ProcessClassLoader;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 252 $ $Date: 2008-12-22 18:24:21 -0200 (Mon, 22 Dec 2008) $
 */
public class OsgiProcessClassLoader extends ProcessClassLoader {

    private static ProcessFragmentRegistry processRegistry;

    static synchronized void setProcessRegistry(ProcessFragmentRegistry processRegistry) {

        OsgiProcessClassLoader.processRegistry = processRegistry;
    }

    public OsgiProcessClassLoader(ClassLoader parent, ProcessDefinition processDefinition) {
        super(parent, processDefinition);
    }


    public Class findClass(String name) throws ClassNotFoundException {
        Class clazz = null;

        try {
            clazz = processRegistry.findProcessActionClass(name);
        } catch (ClassNotFoundException e) {
            // fallback onto process classloader
            clazz = super.findClass(name);
        }

        return clazz;
    }

}
