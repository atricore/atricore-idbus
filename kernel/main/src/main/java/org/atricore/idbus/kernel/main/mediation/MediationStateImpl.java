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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.mediation.MediationState;
import org.atricore.idbus.kernel.main.mediation.state.LocalState;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MediationStateImpl implements MediationState {

    private static final Log logger = LogFactory.getLog(MediationStateImpl.class);

    private Map<String, String> transientVars = new HashMap<String, String>();

    private Map<String, String> remoteVars = new HashMap<String, String>();
    private Set<String> removedRemoteVars = new HashSet<String>();

    private LocalState localState;

    public MediationStateImpl(LocalState localState) {
        if (logger.isTraceEnabled())
            logger.trace("Creating Mediation State with local state : " + localState);
        this.localState = localState;
    }

    public boolean isLocalStateAvailable() {
        return localState != null;
    }

    public String getTransientVariable(String name) {
        return transientVars.get(name);
    }

    public Object getLocalVariable(String name) {
        if (localState == null)
            throw new IllegalStateException("Local state not supported for this message");
        return localState.getValue(name);
    }

    public void setLocalVariable(String name, Object value) {
        if (localState == null)
            throw new IllegalStateException("Local state not supported for this message");
        localState.setValue(name, value);
    }

    public void removeLocalVariable(String name) {
        if (localState == null)
            throw new IllegalStateException("Local state not supported for this message");
        localState.removeValue(name);
    }

    public String getRemoteVariable(String name) {
        return remoteVars.get(name);
    }

    public void setRemoteVariable(String name, String value) {
        remoteVars.put(name, value);
    }

    public void removeRemoteVariable(String name) {
        remoteVars.remove(name);
        removedRemoteVars.add(name);
    }

    public Collection<String> getLocalVarNames() {
        return localState.getKeys();
    }

    public Collection<String> getRemoteVarNames() {
        return remoteVars.keySet();
    }

    public Collection<String> getTransientVarNames() {
        return transientVars.keySet();
    }

    public Collection<String> getRemovedRemoteVarNames() {
        return removedRemoteVars;
    }

    public Map<String, String> getRemoteVars() {
        return remoteVars;
    }

    public Map<String, String> getTransientVars() {
        return transientVars;
    }

    public LocalState getLocalState() {
        return localState;
    }
}

