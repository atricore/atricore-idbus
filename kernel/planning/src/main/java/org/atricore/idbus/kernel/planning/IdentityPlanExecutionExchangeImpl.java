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
import java.util.Map;

/**
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: IdentityMediator.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public class IdentityPlanExecutionExchangeImpl implements IdentityPlanExecutionExchange {

    protected IdentityArtifact in;

    protected IdentityArtifact out;

    protected IdentityPlanExecutionStatus status;

    protected Map<String, Object> properties = new java.util.HashMap<String, Object>();

    protected Map<String, Object> transientProperties = new java.util.HashMap<String, Object>();


    public IdentityArtifact getIn() {
        return this.in;
    }

    public void setIn(IdentityArtifact in) {
        this.in = in;
    }

    public IdentityArtifact getOut() {
        return this.out;
    }

    public void setOut(IdentityArtifact out) {
        this.out = out;
    }

    public IdentityPlanExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(IdentityPlanExecutionStatus status) {
        this.status = status;
    }

    public Object getProperty(String name) {
        return properties.get(name);
    }

    public Object setProperty(String name, Object value) {
        return properties.put(name, value);
    }

    public Object removeProperty(String name) {
        return properties.remove(name);
    }

    public Collection<String> getPropertyNames() {
        return properties.keySet();
    }
    
    public Object getTransientProperty(String name) {
        return transientProperties.get(name);
    }

    public Object setTransientProperty(String name, Object value) {
        return transientProperties.put(name, value);
    }

    public Object removeTransientProperty(String name) {
        return transientProperties.remove(name);
    }

    public Collection<String> getTransientPropertyNames() {
        return transientProperties.keySet();
    }    
}
