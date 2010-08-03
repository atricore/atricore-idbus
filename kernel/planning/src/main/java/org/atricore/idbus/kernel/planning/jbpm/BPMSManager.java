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


import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;

import java.util.Map;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 13 $ $Date: 2007-11-01 19:50:10 -0300 (Thu, 01 Nov 2007) $
 */
public interface BPMSManager {

    /**
     * Start a new process.
     *
     * @param processType      - the type of process to start
     * @param processVariables - optional process variables/parameters to set
     * @return an object representing the new process
     */
    public Object startProcess(Object processType, Map processVariables, Map transientVariables) throws Exception;

    /**
     * Advance an already-running process.
     *
     * @param processId        - an ID which identifies the running process
     * @param transition       - optionally specify which transition to take from the
     *                         current state
     * @param processVariables - optional process variables/parameters to set
     * @return an object representing the process in its new (i.e., advanced) state
     */
    public Object advanceProcess(Object processId, Object transition, Map processVariables, Map transientVariables) throws Exception;

    /**
     * Update the variables/parameters for an already-running process.
     *
     * @param processId        - an ID which identifies the running process
     * @param processVariables - process variables/parameters to set
     * @return an object representing the process in its new (i.e., updated) state
     */
    public Object updateProcess(Object processId, Map processVariables, Map transientVariables) throws Exception;

    /**
     * Abort (end abnormally) a running process.
     *
     * @param processId - an ID which identifies the running process
     */
    public void abortProcess(Object processId) throws Exception;

    /**
     * Looks up an already-running process.
     *
     * @return an object representing the process
     */
    public Object lookupProcess(Object processId) throws Exception;

    /**
     * @return an ID which identifies the given process.
     */
    public Object getId(Object process) throws Exception;

    /**
     * @return the current state of the given process.
     */
    public Object getState(Object process) throws Exception;

    /**
     * @return true if the given process has ended.
     */
    public boolean hasEnded(Object process) throws Exception;

    /**
     * @return true if the object is a valid process
     */
    public boolean isProcess(Object obj) throws Exception;


    Map getProcessVariables(Object processId) throws Exception;

    /**
     * Deploy a new process definition.
     */
    String deployProcessFromStream(InputStream processDefinition)
            throws FileNotFoundException, IOException;

    /**
     * Deploy a new process definition from a process descriptor identified by its name.
     *
     * @param processDescriptorName
     * @throws IdentityPlanningException
     */
    String deployProcessDefinition(String processDescriptorName) throws IdentityPlanningException;


    void perform(String processType, String processDescriptorName, IdentityPlanExecutionExchange ex) throws IdentityPlanningException;
}
