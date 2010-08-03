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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * This inputstream can receive a classloader
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ClassLoadingAwareObjectInputStream extends ObjectInputStream {

    private static final ClassLoader FALLBACK_CLASS_LOADER = ClassLoadingAwareObjectInputStream.class.getClassLoader();

    private ClassLoader cl;

    /** <p>Maps primitive type names to corresponding class objects.</p> */
    private static final HashMap<String, Class> primClasses = new HashMap<String, Class>(8, 1.0F);

    /**
     * This will use the recieved classloader
     * @param in
     * @throws IOException
     */
    public ClassLoadingAwareObjectInputStream(ClassLoader cl, InputStream in) throws IOException {
        super(in);
        this.cl = cl;
    }

    /**
     * This will use context classloader
     * @param in
     * @throws IOException
     */
    public ClassLoadingAwareObjectInputStream(InputStream in) throws IOException {
        super(in);
        cl = Thread.currentThread().getContextClassLoader();
    }


    protected Class resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        return load(classDesc.getName(), cl);
    }

    protected Class resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {

        Class[] cinterfaces = new Class[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            cinterfaces[i] = load(interfaces[i], cl);
        }

        try {
            return Proxy.getProxyClass(cinterfaces[0].getClassLoader(), cinterfaces);
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException(null, e);
        }
    }

    private Class load(String className, ClassLoader cl)
            throws ClassNotFoundException {

        try {
            return Class.forName(className, false, cl);
        } catch (ClassNotFoundException e) {
            final Class clazz = (Class) primClasses.get(className);
            if (clazz != null) {
                return clazz;
            } else {
                return Class.forName(className, false, FALLBACK_CLASS_LOADER);
            }
        }
    }



    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
    }

}

