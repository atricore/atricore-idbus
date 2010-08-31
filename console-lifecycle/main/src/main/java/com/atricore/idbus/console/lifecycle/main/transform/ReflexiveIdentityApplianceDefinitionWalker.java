/*
 * Copyright (c) 2009., Atricore Inc.
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

package com.atricore.idbus.console.lifecycle.main.transform;

import com.atricore.idbus.console.lifecycle.main.domain.metadata.Connection;
import com.atricore.idbus.console.lifecycle.main.domain.metadata.IdentityApplianceDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ReflexiveIdentityApplianceDefinitionWalker implements IdentityApplianceDefinitionWalker {

private static final Log logger = LogFactory.getLog(ReflexiveIdentityApplianceDefinitionWalker.class);

    public Object[] walk(IdentityApplianceDefinition idAppliance, IdentityApplianceDefinitionVisitor visitor) {
        return walkAny(idAppliance, visitor, new HashSet<Object>());
    }

    /**
     * This actually walks the tree.  Because it uses reflection, you can use any type of visitors/trees.
     *
     * @param idApplianceElement the tree instance
     * @param visitor   the idbux visitor instance
     * @return the object returned by the visitor instance callback <code>leave</code>
     */
    protected Object[] walkAny(Object idApplianceElement,
                             IdentityApplianceDefinitionVisitor visitor,
                             Set<Object> stack) {
        
        if (idApplianceElement == null)
            return null;

        if (stack.contains(idApplianceElement)) {

            if (logger.isTraceEnabled())
                logger.trace("Element already processed " + idApplianceElement);

            return null;
        }

        if (logger.isTraceEnabled())
            logger.trace("processing    -> " + idApplianceElement.getClass().getSimpleName());
                          
        try {

            // -------------------------------------------------------------------------
            // Execute visitor.arrive(...)
            // -------------------------------------------------------------------------

            // Connections can be navigated several times ... this could be better, but works for now :)
            // See also TransformerVisitor, it also contains specific logic to handle connections
            if (!(idApplianceElement instanceof Connection))
                stack.add(idApplianceElement);

            // Look for an "arrive" method in the visitor for the current node
            //Method arrive = visitor.getClass().getMethod("arrive", idApplianceElement.getClass());
            Method arrive = lookupVisitorMethod(visitor, "arrive", idApplianceElement.getClass(), new Class[0]);

            if (logger.isTraceEnabled())
                logger.trace("arrive        -> " + idApplianceElement.getClass().getSimpleName());

            arrive.invoke(visitor, idApplianceElement);

            Object[] results = null;
            Object[] applianceChildren = getApplianceChildren(idApplianceElement);

            if (applianceChildren != null) {

                results = new Object[applianceChildren.length];

                for (int i = 0; i < applianceChildren.length; i++) {

                    Object resultOfPrevChild = i == 0 ? null : results[i - 1];

                    // -------------------------------------------------------------------------
                    // Execute visitor.walkNextChild(...)
                    // -------------------------------------------------------------------------

                    // Look for an "walkNextChild" method in the visitor for the current node
                    // Method walkNextChild = visitor.getClass().getMethod("walkNextChild", idApplianceElement.getClass(), Object.class, Integer.TYPE);
                    Method walkNextChild = lookupVisitorMethod(visitor, "walkNextChild", idApplianceElement.getClass(), new Class[] {Object.class, Object.class, Integer.TYPE});

                    if (logger.isTraceEnabled())
                        logger.trace("walkNextChild -> " + idApplianceElement.getClass().getSimpleName());

                    Boolean doWalk = (Boolean) walkNextChild.invoke(visitor, new Object[]{idApplianceElement, applianceChildren[i], resultOfPrevChild, i});
                    if (!doWalk) {
                        if (logger.isTraceEnabled())
                            logger.trace("skip Child    -> " + applianceChildren[i].getClass().getSimpleName());
                        continue;
                    }

                    Object[] r = walkAny(applianceChildren[i], visitor, stack);

                    results[i] = r;
                }
            }

            if (logger.isTraceEnabled())
                logger.trace("leave         -> " + idApplianceElement.getClass().getSimpleName());

            // Look for an "leave" method in the visitor for the current node
            // Method leave = visitor.getClass().getMethod("leave", idApplianceElement.getClass(), Object[].class);
            Method leave = lookupVisitorMethod(visitor, "leave", idApplianceElement.getClass(), new Class[] {Object[].class});
            return (Object[]) leave.invoke(visitor, new Object[]{idApplianceElement, results});

        } catch (InvocationTargetException e) {
            // TODO !
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            logger.debug("No method found to walk element " + idApplianceElement + " [" + e.getMessage() + "]");
            
        } catch (IllegalAccessException e) {
            // TODO !
            throw new RuntimeException(e);
        }

        return null;
    }


    public Object[] getApplianceChildren(Object idApplianceElement) {
        try {
            // List of children
            List children = new ArrayList();

            // Let's take a look at getters ...

            Method[] ms = idApplianceElement.getClass().getMethods();

            if (logger.isTraceEnabled())
                logger.trace("Looking for children " + idApplianceElement);

            for (Method method : ms) {

                // Method should return a type
                Class type = method.getReturnType();

                // This method is not a getter for a child element
                if (type == null || !method.getName().startsWith("get")) {
                    continue;
                }

                // Enums are not supported.
                if (type.isEnum())
                    continue;

                // This is a getter could return a Child elements ...

                if (type.getPackage() != null && type.getPackage().getName().equals("com.atricore.idbus.console.lifecycle.main.domain.metadata") &&
                        method.getParameterTypes().length == 0) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using method (object) " + method.getName());

                    Object child = method.invoke(idApplianceElement);
                    if (child != null && !child.getClass().isEnum()) {
                        children.add(child);
                    }

                } else if (type.isAssignableFrom(java.util.List.class) && method.getParameterTypes().length == 0) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using method (list) " + method.getName());

                    List c = (List) method.invoke(idApplianceElement);
                    if (c != null) {
                        for (Object o : c) {
                            if (o.getClass().getPackage().getName().equals("com.atricore.idbus.console.lifecycle.main.domain.metadata")
                                    && !o.getClass().isEnum()) {
                                children.add(o);
                            }
                        }
                    }

                } else if (type.isAssignableFrom(java.util.Set.class) && method.getParameterTypes().length == 0) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using method (set) " + method.getName());

                    Set c = (Set) method.invoke(idApplianceElement);
                    if (c != null) {
                        for (Object o : c) {
                            if (o.getClass().getPackage().getName().equals("com.atricore.idbus.console.lifecycle.main.domain.metadata")
                                    && !o.getClass().isEnum()) {
                                children.add(o);
                            }
                        }
                    }

                } else if (type.isAssignableFrom(java.util.Collection.class) && method.getParameterTypes().length == 0) {

                    if (logger.isTraceEnabled())
                        logger.trace("Using method (collection) " + method.getName());

                    Collection c = (Collection) method.invoke(idApplianceElement);
                    if (c != null) {
                        for (Object o : c) {
                            if (o.getClass().getPackage().getName().equals("com.atricore.idbus.console.lifecycle.main.domain.metadata")
                                    && !o.getClass().isEnum()) {
                                children.add(o);
                            }
                        }
                    }

                } else {
                    if (logger.isTraceEnabled())
                        logger.trace("Ignoring method " + method.getName());
                }
            }

            if (logger.isTraceEnabled())
                logger.trace("Retrieved " + children.size() + " children nodes");

            return children.toArray(new Object[children.size()]);

        } catch (IllegalAccessException e) {
            // TODO !
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            // TODO !
            throw new RuntimeException(e);
        }
    }


    protected Method lookupVisitorMethod(IdentityApplianceDefinitionVisitor target, String methodName, Class nodeType, Class[] otherArgs) throws NoSuchMethodException {

        Method[] methods = target.getClass().getMethods();

        // Build an array with all args
        Class[] allArgs = new Class[otherArgs.length + 1];
        allArgs[0] = nodeType;
        for (int i = 0; i < otherArgs.length; i++) {
            Class otherArg = otherArgs[i];
            allArgs[i+1] = otherArg;
        }

        // Look for the specific method
        try {
            return target.getClass().getMethod(methodName, allArgs);
        } catch (NoSuchMethodException e) {
            // No luck, look for overloaded methods ....
        }

        // Try methods that receive super classes of the nodeType
        for (Method method : methods) {

            // It has to match the method name
            if (!method.getName().equals(methodName))
                continue;

            // It has to return Object
            if (!method.getReturnType().getClass().getName().equals("java.lang.Object"))
                continue;

            // It has to receive the same number of parameters
            Class[] params = method.getParameterTypes();
            if (params.length != otherArgs.length + 1)
                    continue;
            // The first parameter can be a superclass/interface extended/implemented by node type
            if (!params[0].isAssignableFrom( nodeType))
                continue;

            boolean valid = true;
            for (int i = 1; i < allArgs.length; i++) {

                if (!params[i].isAssignableFrom(allArgs[i])) {
                    valid = false;
                    break;
                }
            }

            if (!valid)
                continue;

            // Gotcha!
            return method;
        }

        throw new NoSuchMethodException(methodName);

    }


}
