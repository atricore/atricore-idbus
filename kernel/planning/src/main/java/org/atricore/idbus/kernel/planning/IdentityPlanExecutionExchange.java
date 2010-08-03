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

package org.atricore.idbus.kernel.planning;

import java.util.Collection;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: IdentityMediator.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public interface IdentityPlanExecutionExchange {

    IdentityArtifact getIn();

    void setIn(IdentityArtifact in);

    IdentityArtifact getOut();

    void setOut(IdentityArtifact out);

    IdentityPlanExecutionStatus getStatus();

    void setStatus(IdentityPlanExecutionStatus status);

    Object getProperty(String name);

    Object setProperty(String name, Object value);

    Object removeProperty(String name);

    Collection<String> getPropertyNames();

    Object getTransientProperty(String name);

    Object setTransientProperty(String name, Object value);

    Object removeTransientProperty(String name);

    Collection<String> getTransientPropertyNames();


}
