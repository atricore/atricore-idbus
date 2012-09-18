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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmException;
import org.jbpm.JbpmContext;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.*;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.log.ProcessStateLog;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;
import org.jbpm.util.Clock;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Rev: 278 $ $Date: 2008-12-31 19:42:22 -0200 (Wed, 31 Dec 2008) $
 */
public class ProcessFragmentState extends Node implements Parsable {

    private static final long serialVersionUID = 1L;

    protected final transient Log logger = LogFactory.getLog(getClass());

    static ProcessFragmentResolver defaultProcessFragmentResolver;

    static BPMSManager bpmsManager;

    public static synchronized void setDefaultProcessFragmentResolver(ProcessFragmentResolver processFragmentResolver) {
        defaultProcessFragmentResolver = processFragmentResolver;
    }

    public static synchronized void setBpmsManager(BPMSManager m) {
        bpmsManager = m;
    }

    protected Set<VariableAccess> variableAccesses = null;
    protected String processFragmentName = null;
    protected String processFragmentLifecycle = null;
    protected String processFragmentPhase = null;
    protected ProcessDefinition processFragmentDefinition = null;

    public ProcessFragmentState() {
        super();
    }

// event types //////////////////////////////////////////////////////////////

    public static final String[] supportedEventTypes = new String[]{Event.EVENTTYPE_SUBPROCESS_CREATED, Event.EVENTTYPE_SUBPROCESS_END,
            Event.EVENTTYPE_NODE_ENTER, Event.EVENTTYPE_NODE_LEAVE, Event.EVENTTYPE_BEFORE_SIGNAL, Event.EVENTTYPE_AFTER_SIGNAL};

    public String[] getSupportedEventTypes() {
        return supportedEventTypes;
    }

    // xml //////////////////////////////////////////////////////////////////////

    public void read(Element processStateElement, JpdlXmlReader jpdlReader) {

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP read fragment start");

        log.debug("Start reading JPDL from XML ...");

        ProcessFragment processFragment;
        Element processFragmentElement = processStateElement.element("process-fragment");

        if (processFragmentElement != null) {

            processFragmentLifecycle = processFragmentElement.attributeValue("lifecycle");
            processFragmentPhase = processFragmentElement.attributeValue("phase");

            ProcessFragmentResolver processFragmentResolver = getProcessFragmentResolver();

            try {
                
                processFragment = processFragmentResolver.findProcessFragment(processFragmentElement);

                if (processFragment != null) {
                    processFragmentName = processFragment.getName();
                    processFragmentDefinition = processFragment.getDefinition();

                    JbpmContext jbpmContext = JbpmContext.getCurrentJbpmContext();

                    if (jbpmContext != null) {

                        logger.debug("Contributions found for lifecycle [" + processFragmentLifecycle + "], " +
                                    "phase [" + processFragmentPhase + "]"
                                   );

                        logger.debug("Deploying Process Fragment Definition '" +
                                processFragmentDefinition.getName() + "'");

                        jbpmContext.deployProcessDefinition(processFragmentDefinition);

                        // Dump this process fragment for debugging purposes.
                        if (logger.isDebugEnabled()) {
                            
                            logger.debug("Process Fragment ["+processFragmentName+"] Nodes:");

                            java.util.List<Node> nodes = processFragmentDefinition.getNodes();
                            for (Node node : nodes) {
                                
                                StringBuffer sb = new StringBuffer();
                                sb.append("Node: ").append(node.getFullyQualifiedName());

                                sb.append(" from: [");
                                java.util.Set<Transition> froms = node.getArrivingTransitions();
                                if (froms != null) {
                                    String prefix = "";
                                    for (Transition t : froms) {
                                        sb.append(prefix).append(t.getFrom().getFullyQualifiedName());
                                        prefix = ",";
                                    }
                                }
                                sb.append("]");

                                java.util.List<Transition> tos = node.getLeavingTransitions();
                                sb.append(" to: [");
                                if (tos != null) {
                                    String prefix = "";
                                    for (Transition t : tos) {
                                        if (t.getTo() != null)
                                            sb.append(prefix).append(t.getTo().getFullyQualifiedName());
                                        else
                                            sb.append(prefix).append("UNRESOLVED");
                                        prefix = ",";
                                    }
                                }
                                sb.append("]");

                                logger.debug(sb);
                            }
                        }

                        logger.debug("Deployed Process Fragment Definition " + processFragmentDefinition.getName());
                    }
                } else {
                    logger.debug("No contributions found for lifecycle [" + processFragmentLifecycle + "], " +
                                "phase [" + processFragmentPhase + "]"
                               );
                }
            } catch (JbpmException e) {
                jpdlReader.addWarning(e.getMessage());
            }

        }

        if (processFragmentDefinition != null) {
            log.debug("processfragment for process-state '" + name + "' bound to '" +
                    processFragmentDefinition.getName() + "'");
        } 

        // Fill variable accesses

        this.variableAccesses = new HashSet<VariableAccess>();
        java.util.List vas = jpdlReader.readVariableAccesses(processStateElement);
        for (Object va : vas) {
            this.variableAccesses.add((VariableAccess) va);
        }

        log.debug("End reading JPDL from XML");

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP read fragment end");

    }

    private ProcessFragmentResolver getProcessFragmentResolver() {
        ProcessFragmentResolver processFragmentResolver = defaultProcessFragmentResolver;
        /* TODO : Check why this restarts all spring setups!
        if (JbpmConfiguration.Configs.hasObject("jbpm.process.fragment.resolver")) {
            processFragmentResolver = (ProcessFragmentResolver) JbpmConfiguration.Configs.getObject("jbpm.process.fragment.resolver");
        } */
        return processFragmentResolver;
    }

    public void execute(ExecutionContext executionContext) {
        ProcessFragment processFragment;
        Token superProcessToken = executionContext.getToken();
        ProcessDefinition usedProcessFragmentDefinition = processFragmentDefinition;

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment start " + processFragmentName);

        ProcessFragmentRegistry pfr;
        String pdn;
        if (processFragmentName != null) {

            pfr = (ProcessFragmentRegistry) executionContext.getContextInstance().getTransientVariable(Constants.VAR_PFR);
            if (pfr == null) {
                throw new NullPointerException("No ProcessFragmentRegistry instance found as transient variable ["+ Constants.VAR_PFR +"] in fragment " + this.processFragmentName);
            }

            pdn = (String) executionContext.getContextInstance().getTransientVariable(Constants.VAR_PDN);
            if (pdn == null) {
                throw new NullPointerException("No Process Descriptor Name instance found as transient variable ["+ Constants.VAR_PDN +"] in fragment " + this.processFragmentName);
            }

            processFragment = pfr.lookupProcessFragment(processFragmentName);
            ProcessDescriptor currentProcessDescriptor = pfr.lookupProcessDescriptor(pdn);
            boolean isProcessFragmentActive = currentProcessDescriptor.isActive(processFragment.getName());
            if (isProcessFragmentActive) {

                if (logger.isTraceEnabled())
                    logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment lookup ok " + processFragmentName);

                logger.debug("Contributions found for lifecycle [" + processFragmentLifecycle + "], " +
                            "phase [" + processFragmentPhase + "]. Spawning enabled fragment " + processFragment.getName());

                // create the processfragment
                ProcessInstance processFragmentInstance = superProcessToken.createSubProcessInstance(usedProcessFragmentDefinition);

                if (logger.isTraceEnabled())
                    logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment subprocess ok " + processFragmentName);

                // fire the processfragment created event
                fireEvent(Event.EVENTTYPE_SUBPROCESS_CREATED, executionContext);

                if (logger.isTraceEnabled())
                    logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment fired event " + processFragmentName);

                // Copy all transient variables
                ContextInstance superContextInstance = executionContext.getContextInstance();
                ContextInstance subContextInstance = processFragmentInstance.getContextInstance();


                for (Object var : superContextInstance.getTransientVariables().keySet()) {
                    logger.debug("Copying super process transient var '" + var + "'");
                }
                subContextInstance.setTransientVariables(superContextInstance.getTransientVariables());

                // feed the readable variableInstances
                if ((variableAccesses != null) && (!variableAccesses.isEmpty())) {

                    // loop over all the variable accesses
                    for (VariableAccess  variableAccess : variableAccesses) {

                        // if this variable access is readable
                        if (variableAccess.isReadable()) {
                            // the variable is copied from the super process variable name
                            // to the sub process mapped name
                            String variableName = variableAccess.getVariableName();
                            Object value = superContextInstance.getVariable(variableName, superProcessToken);
                            
                            String mappedName = variableAccess.getMappedName() != null ?
                                    variableAccess.getMappedName() :
                                    variableAccess.getVariableName();

                            if (value != null) {
                                log.debug("Copying super process var '" + variableName + "' to sub process var '" + mappedName + "': " + value);
                                subContextInstance.setVariable(mappedName, value);
                            } else {
                                log.debug("Super process var '" + variableName + "' has no value, ignoring");
                            }
                        }
                    }
                    
                }

                // TODO : If BPM enigne is removed from planning, this must go way
                // Always add IN/OUT Artifacts as variables!
                if (log.isDebugEnabled())
                    log.debug("Automatically Copying super process var '" + Constants.VAR_IN_IDENTITY_ARTIFACT
                        + "' to sub process var '" + Constants.VAR_IN_IDENTITY_ARTIFACT + "': " +
                        superContextInstance.getVariable(Constants.VAR_IN_IDENTITY_ARTIFACT, superProcessToken));
                subContextInstance.setVariable(Constants.VAR_IN_IDENTITY_ARTIFACT,
                        superContextInstance.getVariable(Constants.VAR_IN_IDENTITY_ARTIFACT, superProcessToken));

                if (log.isDebugEnabled())
                    log.debug("Automatically Copying super process var '" + Constants.VAR_OUT_IDENTITY_ARTIFACT
                        + "' to sub process var '" + Constants.VAR_OUT_IDENTITY_ARTIFACT + "': " +
                        superContextInstance.getVariable(Constants.VAR_OUT_IDENTITY_ARTIFACT, superProcessToken));
                subContextInstance.setVariable(Constants.VAR_OUT_IDENTITY_ARTIFACT,
                        superContextInstance.getVariable(Constants.VAR_OUT_IDENTITY_ARTIFACT, superProcessToken));

                if (logger.isTraceEnabled())
                    logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment before signal " + processFragmentName);

                processFragmentInstance.signal();

                if (!processFragmentInstance.hasEnded()) {
                    logger.warn("Identity Plan process fragment '"+processFragmentName+"' [" + processFragmentInstance.getId() + "] has not ended! Check your process definition");
                }

                if (logger.isTraceEnabled())
                    logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment after signal " + processFragmentName);


                logger.debug("Spawned enabled Process Fragment " + processFragment.getName());

            } else {
                if (logger.isDebugEnabled()) {
                    StringBuffer actives = new StringBuffer();
                    Collection<String> activeFragments = currentProcessDescriptor.getActiveProcessFragments();
                    for (String name : activeFragments) {
                        actives.append(name).append(",");
                    }
                    logger.debug("Process Fragment " + processFragment.getName() + " not enabled. Skipping execution.  " +
                            "Active fragments are ["+actives+"]");
                }
                leave(executionContext, getDefaultLeavingTransition());
            }
        } else {
            logger.debug("No contributions found for lifecycle [" + processFragmentLifecycle + "], " +
                        "phase [" + processFragmentPhase + "]. Skipping execution..."
                       );
            leave(executionContext, getDefaultLeavingTransition());
        }

        if (logger.isTraceEnabled())
            logger.trace("IDBUS-PERF METHODC [" + Thread.currentThread().getName() + "] /bpm.startProcess STEP execute fragment end");

    }


    public void leave(ExecutionContext executionContext, Transition transition) {
        ProcessInstance processFragmentInstance = executionContext.getSubProcessInstance();

        if (processFragmentInstance != null) {
            Token superProcessToken = processFragmentInstance.getSuperProcessToken();

            // feed the readable variableInstances
            if ((variableAccesses != null) && (!variableAccesses.isEmpty())) {

                ContextInstance superContextInstance = executionContext.getContextInstance();
                ContextInstance subContextInstance = processFragmentInstance.getContextInstance();

                // loop over all the variable accesses
                Iterator iter = variableAccesses.iterator();
                while (iter.hasNext()) {
                    VariableAccess variableAccess = (VariableAccess) iter.next();
                    // if this variable access is writable
                    if (variableAccess.isWritable()) {
                        // the variable is copied from the sub process mapped name
                        // to the super process variable name
                        String mappedName = variableAccess.getMappedName();
                        Object value = subContextInstance.getVariable(mappedName);
                        String variableName = variableAccess.getVariableName();

                        if (value != null) {
                            log.debug("copying sub process var '" + mappedName + "' to super process var '" + variableName + "': " + value);
                            superContextInstance.setVariable(variableName, value, superProcessToken);
                        }
                    }
                }
            }

            // fire the processfragment ended event
            fireEvent(Event.EVENTTYPE_SUBPROCESS_END, executionContext);

            // remove the processfragment reference
            superProcessToken.setSubProcessInstance(null);

            // We replaced the normal log generation of super.leave() by creating the log here
            // and overriding the addNodeLog method with an empty version
            superProcessToken.addLog(new ProcessStateLog(this, superProcessToken.getNodeEnter(), Clock.getCurrentTime(), processFragmentInstance));

        }

        // call the processFragmentEndAction
        super.leave(executionContext, getDefaultLeavingTransition());
    }

    // We replaced the normal log generation of super.leave() by creating the log above in the leave method
    // and overriding the addNodeLog method with an empty version
    protected void addNodeLog(Token token) {
    }


    private static Log log = LogFactory.getLog(ProcessFragmentState.class);
}
