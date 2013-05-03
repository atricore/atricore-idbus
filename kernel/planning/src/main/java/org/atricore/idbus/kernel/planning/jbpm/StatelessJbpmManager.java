package org.atricore.idbus.kernel.planning.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionStatus;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.instantiation.ProcessClassLoaderFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.xml.sax.InputSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class StatelessJbpmManager implements BPMSManager, Constants, InitializingBean, ApplicationContextAware {

    protected static transient Log logger = LogFactory.getLog(StatelessJbpmManager.class);

    protected JbpmConfiguration jbpmConfiguration = null;

    protected ProcessFragmentRegistry processFragmentRegistry;

    protected ApplicationContext applicationContext;

    private Map<String, ProcessDefinition> processDefinitions = new ConcurrentHashMap<String, ProcessDefinition>();

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

    public StatelessJbpmManager() throws Exception {
        jbpmConfiguration = JbpmConfiguration.getInstance("org/atricore/idbus/kernel/planning/jbpm/jbpm.cfg.xml");
    }

    public StatelessJbpmManager(JbpmConfiguration jbpmConfiguration) {
        setJbpmConfiguration(jbpmConfiguration);
    }

    public void afterPropertiesSet() throws Exception {
        // Enable OSGi-based process fragment resolution
        ProcessFragmentState.setDefaultProcessFragmentResolver(
                new SpringProcessFragmentResolver(processFragmentRegistry)
        );

        ProcessFragmentState.setBpmsManager(this);

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
            processInstance = jbpmContext.getGraphSession().loadProcessInstance(toLong(processId));
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
    public Object startProcess(Object processType) throws Exception {
        return startProcess(processType, /* processVariables */null, null);
    }

    /**
     * Start a new process.
     *
     * @return the newly-created ProcessInstance
     */
    public Object startProcess(Object processType, Map processVariables, Map transientVariables) throws Exception {
        ProcessInstance processInstance = null;
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();

        try {
            // Some access needs to be serialized:
            ProcessDefinition processDefinition = null;
            processDefinition = jbpmContext.getGraphSession().findLatestProcessDefinition(
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
            //jbpmContext.save(processInstance);

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
    public Object advanceProcess(Object processId) throws Exception {
        return advanceProcess(processId, /* transition */null, /* processVariables */null, /* transient variables */ null);
    }

    /**
     * Advance a process instance one step.
     *
     * @return the updated ProcessInstance
     */
    public Object advanceProcess(Object processId, Object transition, Map processVariables, Map transientVariables)
            throws Exception {
        ProcessInstance processInstance = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession().loadProcessInstance(toLong(processId));

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
    public Object updateProcess(Object processId, Map processVariables, Map transientVariables) throws Exception {
        ProcessInstance processInstance = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession().loadProcessInstance(toLong(processId));

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
    public void abortProcess(Object processId) throws Exception {
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

    public void destroyProcess(Object processId) {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            jbpmContext.getGraphSession().deleteProcessInstance(toLong(processId));

        } catch (Exception e) {
            jbpmContext.setRollbackOnly();
            logger.error(e.getMessage(), e);
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
    public Map getProcessVariables(Object processId) throws Exception {

        Map processVariables = null;

        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            ProcessInstance processInstance = null;
            // Look up the process instance from the database.
            processInstance = jbpmContext.getGraphSession().loadProcessInstance(toLong(processId));

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
        throw new UnsupportedOperationException("Not supported by this Manager");

        /*
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();

        ProcessDefinition processDefinition;
        processDefinition = parseXmlInputStream(processDefinitionIs);
        String processType = processDefinition.getName();

        try {
            jbpmContext.deployProcessDefinition(processDefinition);
        } finally {
            jbpmContext.close();
        }

        return processType;
        */

    }

    protected ProcessDefinition parseXmlInputStream(ProcessDescriptor pd, InputStream procesDefinitionIs) {
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {
            StatelessJpdlXmlReader jpdlReader = new StatelessJpdlXmlReader(new InputSource(procesDefinitionIs));
            jpdlReader.setFragmentResolver(ProcessFragmentState.getDefaultProcessFragmentResolver());
            jpdlReader.setProcessDescriptor(pd);
            return jpdlReader.readProcessDefinition();
        } finally {
            jbpmContext.close();
        }

    }


    public String deployProcessDefinition(String processDescriptorName) throws IdentityPlanningException {

        String processType = null;
        InputStream descriptorIs = null;
//        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();

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

            // This is the main process descriptor:
            bootstrapProcessFragment = processFragmentRegistry.lookupProcessFragment(bootstrapFragmentName);
            if (bootstrapProcessFragment != null) {

                descriptorIs = bootstrapProcessFragment.getProcessFragmentDescriptor().getInputStream();
                ProcessDefinition processDefinition = parseXmlInputStream(pd, descriptorIs);
                processType = processDefinition.getName();
//                jbpmContext.deployProcessDefinition(processDefinition);

                processDefinitions.put(pd.getName(), processDefinition);
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

//            jbpmContext.close();
        }

        return processType;
    }

    public void perform(String processType, String processDescriptorName, IdentityPlanExecutionExchange ex) throws IdentityPlanningException {

        Map<String, Object> transientVariables = new HashMap<String, Object>();
        Map<String, Object> processVariables = new HashMap<String, Object>();

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

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
        try {

            logger.debug("Starting process '" + processType + "'");

            // process = startProcess(processType, processVariables, transientVariables);
            //processId = getId(process);

            ProcessDefinition processDefinition = processDefinitions.get(processDescriptorName);
            ProcessInstance processInstance =
                    new ProcessInstance(processDefinition);

            ContextInstance contextInstance =
                    processInstance.getContextInstance();

            // Set any process variables.
            if (processVariables != null && !processVariables.isEmpty()) {
                contextInstance.addVariables(processVariables);
            }

            if (transientVariables != null && !transientVariables.isEmpty()) {
                contextInstance.setTransientVariables(transientVariables);
            }

            processInstance.signal();

        } catch (Exception e) {
            ex.setStatus(IdentityPlanExecutionStatus.ERROR);
            throw new IdentityPlanningException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
            jbpmContext.close();
        }

        // TODO : Can be replaced the out/in ?
        IdentityArtifact outIdentityArtifact = (IdentityArtifact) processVariables.get(VAR_OUT_IDENTITY_ARTIFACT);
        ex.setOut(outIdentityArtifact);
        ex.setStatus(IdentityPlanExecutionStatus.SUCCESS);

    }


    public void performOld(String processType, String processDescriptorName, IdentityPlanExecutionExchange ex) throws IdentityPlanningException {

        Map<String, Object> transientVariables = new HashMap<String, Object>();
        Map<String, Object> processVariables = new HashMap<String, Object>();

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

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {

            ProcessClassLoaderFactory f = new OsgiProcessClassLoaderFactory();
            ProcessDefinition processDefinition = this.processDefinitions.get(processDescriptorName);
            ClassLoader pcl = f.getProcessClassLoader(processDefinition);
            Thread.currentThread().setContextClassLoader(pcl);

            ProcessInstance processInstance = new ProcessInstance(processDefinition);
            ContextInstance contextInstance = processInstance.getContextInstance();

            // Set any process variables.
            if (processVariables != null && !processVariables.isEmpty()) {
                contextInstance.addVariables(processVariables);
            }

            if (transientVariables != null && !transientVariables.isEmpty()) {
                contextInstance.setTransientVariables(transientVariables);
            }

            processInstance.signal();
        } catch (Exception e) {
            ex.setStatus(IdentityPlanExecutionStatus.ERROR);
            throw new IdentityPlanningException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
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

    public void completeTask(TaskInstance task) {
        completeTask(task, /* transition */null);
    }

    public void completeTask(TaskInstance task, String transition) {
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
