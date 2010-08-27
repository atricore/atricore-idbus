/*
 * Copyright (c) 2010., Atricore Inc.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class IdApplianceTransformationContext {

    private static final Log logger = LogFactory.getLog(IdApplianceTransformationContext.class);

    private Cycle currentCycle;

    private Phase currentPhase;

    private IdApplianceProject project;

    private IdProjectModule currentModule;

    private Map<String, Object> attrs = new HashMap<String, Object>();

    private Stack nodes = new Stack();

    public IdApplianceTransformationContext(IdApplianceProject project) {
        this.project = project;
    }

    public Cycle getCurrentCycle() {
        return currentCycle;
    }

    public void setCurrentCycle(Cycle currentCycle) {
        this.currentCycle = currentCycle;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public IdApplianceProject getProject() {
        return project;
    }

    public void setCurrentModule(IdProjectModule module) {

        if (logger.isTraceEnabled())
            logger.trace("Current Idau project module " + module);

        this.currentModule = module;
    }

    public IdProjectModule getCurrentModule() {
        return currentModule;
    }

    public void put(String name, Object value) {
        attrs.put(name, value);
    }

    public Object get(String name) {
        return attrs.get(name);
    }

    public void push(Object node) {
        nodes.push(node);
    }

    public Object pop() {
        return nodes.pop();
    }

    public Object peek() {
        return nodes.peek();
    }

    public Enumeration getNodes() {
        return nodes.elements();
    }

    public Object getCurrentNode() {
        return nodes.peek();
    }

    public Object getParentNode() {
        if (!nodes.empty() && nodes.size() > 1)
            return nodes.elementAt(nodes.size() - 2);

        return null;
    }

    public int nodesSize() {
        return nodes.size();
    }
}
