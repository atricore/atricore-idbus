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

import org.osgi.framework.Bundle;

import java.util.Collection;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 278 $ $Date: 2008-12-31 19:42:22 -0200 (Wed, 31 Dec 2008) $
 */
public interface ProcessFragmentRegistry {
    public static final String DEFAULT_PROCESS_FRAGMENT_REGISTRY_ID = "process-fragment-registry" ;

    ProcessFragment lookupProcessFragment(String id);

    Collection<ProcessFragment> lookupBoundProcessFragments(String lifecycle, String phase);

    Collection<ProcessFragment> listProcessFragments();

    Collection<ProcessDescriptor> listProcessDescriptors();

    ProcessDescriptor lookupProcessDescriptor(String name);

    void close();

    Class findProcessActionClass(String qualifiedActionName) throws ClassNotFoundException;


}
