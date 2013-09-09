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

package org.atricore.idbus.kernel.main.mediation;

import org.atricore.idbus.kernel.main.mediation.state.LocalState;

import java.util.Collection;

/**
 * Represents the conversational state during mediation.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: MediationState.java 1359 2009-07-19 16:57:57Z sgonzalez $
 */
public interface MediationState {

    boolean isLocalStateAvailable();

    /**
     * Variables, read-only. Available during the current request.
     */
    String getTransientVariable(String name);

    /**
     * Attributes, read-write. Available during the current request.
     *
     * @return previous value, if any
     */
    Object setAttribute(String attrName, Object attrValue);

    Object getAttribute(String attrName);

    Object removeAttribute(String attrName);

    /**
     * Variable available through different requests, stored locally
     */
    Object getLocalVariable(String name);

    void setLocalVariable(String name, Object value);

    void removeLocalVariable(String name);

    /**
     * Variable available through different requests, stored remotely
     */
    String getRemoteVariable(String name);

    void setRemoteVariable(String name, String value);

    /**
     * @param expires expiration time (GMT)
     */
    void setRemoteVariable(String name, String value, long expires);

    void removeRemoteVariable(String name);

    long getRemoteVarExpiration(String name);

    Collection<String> getLocalVarNames();

    Collection<String> getRemoteVarNames();

    Collection<String> getTransientVarNames();

    Collection<String> getRemovedRemoteVarNames();

    
    LocalState getLocalState();


}
