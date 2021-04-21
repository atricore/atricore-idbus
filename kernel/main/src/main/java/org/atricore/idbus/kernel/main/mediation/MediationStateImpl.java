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
import org.atricore.idbus.kernel.main.mediation.state.LocalState;

import java.util.*;
import java.util.Map;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class MediationStateImpl implements MediationState {

    private static final Log logger = LogFactory.getLog(MediationStateImpl.class);

    private Map<String, Object> transientAttribute = new HashMap<String, Object>();

    private Map<String, String> transientVars = new HashMap<String, String>();

    private Map<String, RemoteVar> remoteVars = new HashMap<String, RemoteVar>();

    private Map<String, RemoteVar> removedRemoteVars = new HashMap<String, RemoteVar>();

    private LocalState localState;

    public MediationStateImpl(LocalState localState) {
        if (logger.isTraceEnabled())
            logger.trace("Creating Mediation State with local state : " + localState);
        this.localState = localState;
    }

    @Override
    public boolean isLocalStateAvailable() {
        return localState != null;
    }

    @Override
    public String getTransientVariable(String name) {
        return transientVars.get(name);
    }

    @Override
    public Object getAttribute(String name) {
        return transientAttribute.get(name);
    }

    @Override
    public Object setAttribute(String name, Object value) {
        return transientAttribute.put(name, value);
    }

    @Override
    public Object removeAttribute(String name) {
        return transientAttribute.remove(name);
    }

    @Override
    public Object getLocalVariable(String name) {
        if (localState == null)
            throw new IllegalStateException("Local state not supported for this message");
        return localState.getValue(name);
    }

    @Override
    public void setLocalVariable(String name, Object value) {
        if (localState == null)
            throw new IllegalStateException("Local state not supported for this message");

        if (value != null && !(value instanceof java.io.Serializable))
            logger.error("Non-serializable variable " + name);

        localState.setValue(name, value);
    }

    @Override
    public void removeLocalVariable(String name) {
        if (localState == null)
            throw new IllegalStateException("Local state not supported for this message");
        localState.removeValue(name);
    }

    @Override
    public String getRemoteVariable(String name) {
        RemoteVar v = remoteVars.get(name);
        if (v != null)
            return v.getValue();
        return null;
    }

    @Override
    public long getRemoteVarExpiration(String name) {
        RemoteVar v = remoteVars.get(name);
        if (v != null)
            return v.getExpires();
        return -1;
    }

    @Override
    public void setRemoteVariable(String name, String value) {
        RemoteVar v = remoteVars.get(name);
        if (v == null) {
            v = new RemoteVar(name, value, 0);
        } else {
            v.setValue(value);
            // Don't update expires property since we don't have a value
        }

        remoteVars.put(name, v);
    }

    @Override
    public void setRemoteVariable(String name, String value, long expires) {
        RemoteVar v = remoteVars.get(name);
        if (v == null) {
            v = new RemoteVar(name, value, expires);
        } else {
            v.setValue(value);
            v.setExpires(expires);
        }

        remoteVars.put(name, v);
    }

    @Override
    public void removeRemoteVariable(String name) {
        RemoteVar v = remoteVars.remove(name);
        removedRemoteVars.put(name, v);
    }

    @Override
    public Collection<String> getLocalVarNames() {
        return localState.getKeys();
    }

    @Override
    public Collection<String> getRemoteVarNames() {
        return remoteVars.keySet();
    }

    @Override
    public Collection<String> getTransientVarNames() {
        return transientVars.keySet();
    }

    @Override
    public Collection<String> getRemovedRemoteVarNames() {
        return removedRemoteVars.keySet();
    }

    @Override
    public long getRemovedRemoteVarExpiration(String name) {
        RemoteVar v = removedRemoteVars.get(name);
        if (v != null)
            return v.getExpires();

        return -1;
    }

    public void setTransientVar(String name, String value) {
        transientVars.put(name, value);
    }

    public void setTransientVars(Map<String, String> vars) {
        transientVars.putAll(vars);
    }

    @Override
    public LocalState getLocalState() {
        return localState;
    }

    public class RemoteVar implements java.io.Serializable {
        private String name;
        private String value;

        private long expires;

        public RemoteVar(String name, String value, long expires) {
            this.name = name;
            this.value = value;
            this.expires = expires;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public long getExpires() {
            return expires;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void setExpires(long expires) {
            this.expires = expires;
        }
    }
}

