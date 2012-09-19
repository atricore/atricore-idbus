package org.atricore.idbus.kernel.planning.jbpm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.xml.JpdlXmlReader;
import org.jbpm.jpdl.xml.Parsable;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class InLineProcessFragmentState extends Node implements Parsable {

    private static final long serialVersionUID = 1L;

    protected final transient Log logger = LogFactory.getLog(getClass());

    static ProcessFragmentResolver defaultProcessFragmentResolver;

    static BPMSManager bpmsManager;

    protected Set<VariableAccess> variableAccesses = null;
    protected String processFragmentName = null;
    protected String processFragmentLifecycle = null;
    protected String processFragmentPhase = null;
    protected ProcessDefinition processFragmentDefinition = null;

    public static synchronized void setDefaultProcessFragmentResolver(ProcessFragmentResolver processFragmentResolver) {
        defaultProcessFragmentResolver = processFragmentResolver;
    }

    public static synchronized void setBpmsManager(BPMSManager m) {
        bpmsManager = m;
    }

    private ProcessFragmentResolver getProcessFragmentResolver() {
        ProcessFragmentResolver processFragmentResolver = defaultProcessFragmentResolver;
        /* TODO : Check why this restarts all spring setups!
        if (JbpmConfiguration.Configs.hasObject("jbpm.process.fragment.resolver")) {
            processFragmentResolver = (ProcessFragmentResolver) JbpmConfiguration.Configs.getObject("jbpm.process.fragment.resolver");
        } */
        return processFragmentResolver;
    }

}
