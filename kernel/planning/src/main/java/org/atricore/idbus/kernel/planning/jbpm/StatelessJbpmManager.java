package org.atricore.idbus.kernel.planning.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.planning.IdentityArtifact;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionExchange;
import org.atricore.idbus.kernel.planning.IdentityPlanExecutionStatus;
import org.atricore.idbus.kernel.planning.IdentityPlanningException;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.jpdl.xml.JpdlXmlReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StatelessJbpmManager extends JbpmManager {

    protected static transient Log logger = LogFactory.getLog(StatelessJbpmManager.class);

    public StatelessJbpmManager() throws Exception {
        // do not initialize a configuration
    }

    public StatelessJbpmManager(JbpmConfiguration jbpmConfiguration) {
        super(jbpmConfiguration);
    }

    @Override
    public String deployProcessDefinition(String processDescriptorName) throws IdentityPlanningException {
        // ignore
        logger.warn("Ignoring deployment request for process definition " + processDescriptorName);
        return null;
    }

    @Override
    public void perform(String processType, String processDescriptorName, IdentityPlanExecutionExchange ex) throws IdentityPlanningException {
        InputStream descriptorIs = null;
        ProcessDefinition processDefinition = null;

        Map<String, Object> transientVariables = new HashMap<String, Object>();
        Map<String, Object> processVariables = new HashMap<String, Object>();

        //JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();

        // Instantiate Process Definition by parsing PDL descriptor bound to fragment
        try {
            ProcessDescriptor pd;
            String bootstrapFragmentName;
            ProcessFragment bootstrapProcessFragment;

            if (logger.isDebugEnabled())
                logger.debug("Parsing process definition " + processDescriptorName);

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

                JpdlXmlReader jpdlReader = new JpdlXmlReader(new InputStreamReader(descriptorIs));
                processDefinition = jpdlReader.readProcessDefinition();
                logger.info("Parsed Process Definition " + processDefinition.getName() + " backed by " +
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
            if (processDefinition != null) {

                logger.debug("Starting process '" + processDefinition.getName() + "'");

                ProcessInstance process = processDefinition.createProcessInstance();

                // Set any process variables.
                if (processVariables != null && !processVariables.isEmpty()) {
                    process.getContextInstance().addVariables(processVariables);
                }

                if (transientVariables != null && !transientVariables.isEmpty()) {
                    process.getContextInstance().setTransientVariables(transientVariables);
                }

                // Leave the start state.
                process.signal();
                Object processId = getId(process);
                Object state = getState(process);
                logger.debug("New " + processDefinition.getName() + " process started, ID = " + processId + ", state:" + state);

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
}
