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

import java.util.Collection;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 252 $ $Date: 2008-12-22 18:24:21 -0200 (Mon, 22 Dec 2008) $
 */
public interface ProcessDescriptor {

    String getName();

    String getBootstrapProcessFragmentName();

    Collection getActiveProcessFragments();

    boolean isActive(String processFragmentName);

    ProcessConfiguration getConfiguration();

    void setProcessFragmentRegistry(ProcessFragmentRegistry processFragmentRegitry);


}
