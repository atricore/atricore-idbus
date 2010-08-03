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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.context.exe.ContextInstance;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.springframework.context.ApplicationContext;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AbstractIdentityPlanActionHandler.java 1336 2009-06-24 18:50:29Z sgonzalez $
 */
public abstract class AbstractIdentityPlanActionHandler implements ActionHandler {

    protected transient Log log = LogFactory.getLog(getClass());

    protected static ThreadLocal<ApplicationContext> ctx =  new ThreadLocal<ApplicationContext>();

    public void execute(ExecutionContext executionContext) throws Exception {

        // Store spring context
        ApplicationContext applicationContext = (ApplicationContext) executionContext.getContextInstance().getTransientVariable(Constants.VAR_APP_CTX);
        ctx.set(applicationContext);

        // Execute action initialization
        doInit(executionContext);

        ContextInstance contextInstance  = executionContext.getContextInstance();
        IdentityArtifact in = (IdentityArtifact) contextInstance.getVariable(Constants.VAR_IN_IDENTITY_ARTIFACT);
        if (in == null)
            log.warn("Input Identity Artifact should not be null!");

        IdentityArtifact out = (IdentityArtifact) contextInstance.getVariable(Constants.VAR_OUT_IDENTITY_ARTIFACT);
        if (out == null)
            log.warn("Output Identity Artifact should not be null!");

        doExecute(in, out, executionContext);

        // Move the process to the next action!
        if (doLeaveNode()) {
            String leavingTransition = getLeavingTransition();
            if (leavingTransition == null)
                executionContext.leaveNode();
            else
                executionContext.leaveNode(leavingTransition);
        } else {
            log.error("Identity Plan Action Handlers should always 'leave' process nodes!");
        }

    }

    protected ApplicationContext getAppliactionContext() {
        return ctx.get();
    }

    protected abstract void doInit(ExecutionContext executionContext) throws Exception;

    protected abstract void doExecute(IdentityArtifact in, IdentityArtifact out, ExecutionContext executionContext) throws Exception;

    protected boolean doLeaveNode() {
        return true;
    }

    protected String getLeavingTransition() {
        return null;
    }
}
