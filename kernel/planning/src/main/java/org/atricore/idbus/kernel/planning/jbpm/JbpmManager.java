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
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionStatus;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * This BPMS Manager is based on JBPM.  The implementation requires JBMP Persistence.
 *
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 222 $ $Date: 2008-12-10 11:37:18 -0300 (Wed, 10 Dec 2008) $
 * @org.apache.xbean.XBean element="bpm-manager"
 */
public class JbpmManager implements BPMSManager, Constants, InitializingBean, ApplicationContextAware {
    protected static transient Log logger = LogFactory.getLog(JbpmManager.class);

    protected JbpmConfiguration jbpmConfiguration = null;

    private ProcessFragmentRegistry processFragmentRegistry;
    private ApplicationContext applicationContext;

    // ///////////////////////////////////////////////////////////////////////////
    // Property accessor and setter methods
    // ///////////////////////////////////////////////////////////////////////////

    public void setProcessFragmentRegistry(ProcessFragmentRegistry processFragmentRegistry) {
        this.processFragmentRegistry = processFragmentRegistry;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Lifecycle methods
    // ///////////////////////////////////////////////////////////////////////////

    public JbpmManager() throws Exception {
        jbpmConfiguration = JbpmConfiguration.getInstance("org/atricore/idbus/kernel/planning/jbpm/jbpm.cfg.xml");
    }

    public JbpmManager(JbpmConfiguration jbpmConfiguration) {
        setJbpmConfiguration(jbpmConfiguration);
    }

    public void afterPropertiesSet() throws Exception {
        // Enable OSGi-based process fragment resolution
        ProcessFragmentState.setDefaultProcessFragmentResolver(
                new SpringProcessFragmentResolver(processFragmentRegistry)
        );

        // Enable OSGi-based Jbpm action class resolution
        OsgiProcessClassLoader.setProcessRegistry(processFragmentRegistry);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Process status / lookup
    // ///////////////////////////////////////////////////////////////////////////

    public boolean isProcess(Object obj) throws Exception {
        return (obj instanceof ProcessInstance);
    }

    public Object getId(Object process) throws Exception {
        return new Long(((ProcessInstance) process).getId());
    }

    public Object getState(Object process) throws Exception {
        return ((ProcessInstance) process).getRootToken().getNode().getName();
    }

    public boolean hasEnded(Object process) throws Exception {
        return ((ProcessInstance) process).hasEnded();
    }

    /**
     * Look up an already-running process instance.
     *
     * @return the ProcessInstance
     */
    public Object lookupProcess(Object processId) throws Exception {
        ProcessInstance processInstance = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession()
                    .loadProcessInstance(toLong(processId));
        }
        finally {
            jbpmContext.close();
        }
        return processInstance;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Process manipulation
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Start a new process.
     *
     * @return the newly-created ProcessInstance
     */
    public synchronized Object startProcess(Object processType) throws Exception {
        return startProcess(processType, /* processVariables */null, null);
    }

    /**
     * Start a new process.
     *
     * @return the newly-created ProcessInstance
     */
    public synchronized Object startProcess(Object processType, Map processVariables, Map transientVariables) throws Exception {
        ProcessInstance processInstance = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();

        try {
            ProcessDefinition processDefinition = jbpmContext.getGraphSession().findLatestProcessDefinition(
                    (String) processType);
            if (processDefinition == null)
                throw new IllegalArgumentException("No process definition found for process " + processType);

            processInstance = new ProcessInstance(processDefinition);

            // Set any process variables.
            if (processVariables != null && !processVariables.isEmpty()) {
                processInstance.getContextInstance().addVariables(processVariables);
            }

            if (transientVariables != null && !transientVariables.isEmpty()) {
                processInstance.getContextInstance().setTransientVariables(transientVariables);
            }

            // Leave the start state.
            processInstance.signal();

            jbpmContext.save(processInstance);

        } catch (Exception e) {
            jbpmContext.setRollbackOnly();
            throw e;
        } finally {
            jbpmContext.close();
        }
        return processInstance;
    }

    /**
     * Advance a process instance one step.
     *
     * @return the updated ProcessInstance
     */
    public synchronized Object advanceProcess(Object processId) throws Exception {
        return advanceProcess(processId, /* transition */null, /* processVariables */null, /* transient variables */ null);
    }

    /**
     * Advance a process instance one step.
     *
     * @return the updated ProcessInstance
     */
    public synchronized Object advanceProcess(Object processId, Object transition, Map processVariables, Map transientVariables)
            throws Exception {
        ProcessInstance processInstance = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession()
                    .loadProcessInstance(toLong(processId));

            if (processInstance.hasEnded()) {
                throw new IllegalStateException(
                        "Process cannot be advanced because it has already terminated, processId = " + processId);
            }

            // Set any process variables.
            // Note: addVariables() will replace the old value of a variable if it
            // already exists.
            if (processVariables != null && !processVariables.isEmpty()) {
                processInstance.getContextInstance().addVariables(processVariables);
            }

            if (transientVariables != null && !transientVariables.isEmpty()) {
                processInstance.getContextInstance().setTransientVariables(transientVariables);
            }

            // Advance the workflow.
            if (transition != null) {
                processInstance.signal((String) transition);
            } else {
                processInstance.signal();
            }

            // Save the process state back to the database.
            jbpmContext.save(processInstance);

        } catch (Exception e) {
            jbpmContext.setRollbackOnly();
            throw e;
        } finally {
            jbpmContext.close();
        }
        return processInstance;
    }

    /**
     * Update the variables for a process instance.
     *
     * @return the updated ProcessInstance
     */
    public synchronized Object updateProcess(Object processId, Map processVariables, Map transientVariables) throws Exception {
        ProcessInstance processInstance = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession()
                    .loadProcessInstance(toLong(processId));

            // Set any process variables.
            // Note: addVariables() will replace the old value of a variable if it
            // already exists.
            if (processVariables != null && !processVariables.isEmpty()) {
                processInstance.getContextInstance().addVariables(processVariables);
            }

            if (transientVariables != null && !transientVariables.isEmpty()) {
                processInstance.getContextInstance().setTransientVariables(processVariables);
            }

            // Save the process state back to the database.
            jbpmContext.save(processInstance);

        } catch (Exception e) {
            jbpmContext.setRollbackOnly();
            throw e;
        } finally {
            jbpmContext.close();
        }
        return processInstance;
    }

    /**
     * Delete a process instance.
     */
    public synchronized void abortProcess(Object processId) throws Exception {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            jbpmContext.getGraphSession().deleteProcessInstance(toLong(processId));

        } catch (Exception e) {
            jbpmContext.setRollbackOnly();
            throw e;
        } finally {
            jbpmContext.close();
        }
    }

    /**
     * Returns the variables for given a process.
     *
     * @param processId
     * @return
     * @throws Exception
     */
    public synchronized Map getProcessVariables(Object processId) throws Exception {

        Map processVariables = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            ProcessInstance processInstance = null;
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession()
                    .loadProcessInstance(toLong(processId));

            processVariables = processInstance.getContextInstance().getVariables();

        } finally {
            jbpmContext.close();
        }

        return processVariables;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Miscellaneous
    // ///////////////////////////////////////////////////////////////////////////


    /**
     * Deploy a new process definition.
     */
    public String deployProcessFromStream(InputStream processDefinitionIs)
            throws FileNotFoundException, IOException {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();

        ProcessDefinition processDefinition;
        processDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionIs);
        String processType = processDefinition.getName();

        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }

        return processType;

    }

    private String deployProcess(ProcessDefinition processDefinition) {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        String processType = processDefinition.getName();

        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }

        return processType;
    }


    public String deployProcessDefinition(String processDescriptorName) throws IdentityPlanningException {

        String processType = null;
        InputStream descriptorIs = null;

        try {

            ProcessDescriptor pd;
            String bootstrapFragmentName;
            ProcessFragment bootstrapProcessFragment;

            if (logger.isDebugEnabled())
                logger.debug("Deploying process definition " + processDescriptorName);

            pd = processFragmentRegistry.lookupProcessDescriptor(processDescriptorName);
            if (pd == null) {

                if (logger.isDebugEnabled()) {
                    // Information about the problem
                    Collection<ProcessDescriptor> frags = processFragmentRegistry.listProcessDescriptors();
                    StringBuffer descriptors = new StringBuffer();
                    for (ProcessDescriptor desc : frags) {
                        descriptors.append(desc.getName()).append(",");
                    }
                    logger.error("No process descriptor found for '" + processDescriptorName +
                            "'.  Registered processes are :" + descriptors);
                }
                throw new IdentityPlanningException("No process definition found for '"+processDescriptorName+"'");
            }

            bootstrapFragmentName = pd.getBootstrapProcessFragmentName();
            if (bootstrapFragmentName == null) {
                throw new IdentityPlanningException("No bootstrap fragment specified for process descriptor ");
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Process definition " + processDescriptorName +
                        " uses bootstrap process fragment " + bootstrapFragmentName);
            }

            bootstrapProcessFragment = processFragmentRegistry.lookupProcessFragment(bootstrapFragmentName);
            if (bootstrapProcessFragment != null) {
                descriptorIs = bootstrapProcessFragment.getProcessFragmentDescriptor().getInputStream();
                processType = deployProcessFromStream(descriptorIs);
                logger.info("Deployed Process Definition " + processType + " backed by " +
                        pd.getBootstrapProcessFragmentName() + " boostrap process fragment");
            } else {
                // Dump some info about the problem
                Collection<ProcessFragment> frags = processFragmentRegistry.listProcessFragments();
                StringBuffer descriptors = new StringBuffer();
                for (ProcessFragment frag : frags) {
                    descriptors.append(frag.getName()).append(",");
                }
                logger.error("No bootstrap process fragment definition found for '" + processDescriptorName +
                        "'.  Registered fragments are :" + descriptors);

                throw new IdentityPlanningException("No bootstrap fragment registered at [" +
                        bootstrapFragmentName + "] Verify definition [" + pd + "]");
            }

        } catch (Exception e) {
            throw new IdentityPlanningException(e);
        } finally {
            // Close descriptors, just in case
            try { if (descriptorIs != null) descriptorIs.close(); } catch (Exception e) { /**/ }
        }

        return processType;
    }

    public void perform(String processType, String processDescriptorName, IdentityPlanExecutionExchange ex) throws IdentityPlanningException {

        Map<String, Object> transientVariables = new HashMap<String, Object>();
        Map<String, Object> processVariables = new HashMap<String, Object>();

        Object process = null;

        logger.debug("IdentityArtifact IN : ["+ex.getIn() + "]");
        logger.debug("IdentityArtifact OUT: ["+ex.getOut() + "]");

        processVariables.put(VAR_IN_IDENTITY_ARTIFACT, ex.getIn());
        processVariables.put(VAR_OUT_IDENTITY_ARTIFACT, ex.getOut());

        // Publish exchange properties as process variables
        for (String key : ex.getPropertyNames()) {
            logger.debug("Copying IDPlan Execution Exchange property '" + key + "' as process variable");
            processVariables.put(key, ex.getProperty(key));
        }

        // Execution context information is available as transient variables
        transientVariables.put(Constants.VAR_PFR, processFragmentRegistry);
        transientVariables.put(Constants.VAR_PDN, processDescriptorName);
        transientVariables.put(Constants.VAR_APP_CTX, applicationContext);
        transientVariables.put(Constants.VAR_IPEE, ex);

        // Other transient variables
        for (String transientVar : ex.getTransientPropertyNames()) {
            transientVariables.put(transientVar,  ex.getTransientProperty(transientVar));
        }

        try {
            if (processType != null) {

                logger.debug("Starting process '" + processType + "'");

                process = startProcess(processType, processVariables, transientVariables);
                Object processId = getId(process);

                Object state = getState(process);
                logger.debug("New " + processType + " process started, ID = " + processId + ", state:" + state);

                if (!hasEnded(process)) {
                    logger.warn("Identity Plan process '"+processType+"' [" + processId + "] has not ended, forcing abortion! Check your process definition");
                    abortProcess(processId);
                }

            } else {
                throw new IllegalArgumentException("Process type is missing, cannot start a new process.");
            }
        } catch (Exception e) {
            ex.setStatus(IdentityPlanExecutionStatus.ERROR);
            throw new IdentityPlanningException(e);
        }

        // TODO : Can be replaced the out/in ?
        IdentityArtifact outIdentityArtifact = (IdentityArtifact) processVariables.get(VAR_OUT_IDENTITY_ARTIFACT);
        ex.setOut(outIdentityArtifact);
        ex.setStatus(IdentityPlanExecutionStatus.SUCCESS);
    }


    public List/* <TaskInstance> */loadTasks(ProcessInstance process) {
        List/* <TaskInstance> */taskInstances = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            taskInstances = jbpmContext.getTaskMgmtSession().findTaskInstancesByToken(
                    process.getRootToken().getId());
        } finally {
            jbpmContext.close();
        }
        return taskInstances;
    }

    public synchronized void completeTask(TaskInstance task) {
        completeTask(task, /* transition */null);
    }

    public synchronized void completeTask(TaskInstance task, String transition) {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            task = jbpmContext.getTaskMgmtSession().loadTaskInstance(task.getId());
            if (transition != null) {
                task.end(transition);
            } else {
                task.end();
            }
        } finally {
            jbpmContext.close();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    // ///////////////////////////////////////////////////////////////////////////

    public JbpmConfiguration getJbpmConfiguration() {
        return jbpmConfiguration;
    }

    public void setJbpmConfiguration(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

    private static long toLong(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Unable to convert null object to long");
        } else if (obj instanceof String) {
            return Long.valueOf((String) obj).longValue();
        } else if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else {
            throw new IllegalArgumentException("Unable to convert object of type: "
                    + obj.getClass().getName() + " to long.");
        }
    }

}
