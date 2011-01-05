package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallOperationsRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InstallOperationsRegistryImpl implements InstallOperationsRegistry {

    private static final Log logger = LogFactory.getLog(InstallOperationsRegistryImpl.class);

    private Set<InstallOperation> operations = new HashSet<InstallOperation>();

    private Map<String, Set<InstallOperation>> operationsByStep = new HashMap<String, Set<InstallOperation>>();

    public Set<InstallOperation> getOperations() {
        return operations;
    }

    public InstallOperation getOperation(String name) {
        for (InstallOperation operation : operations) {
            if (operation.getName().equals(name)) {
                return operation;
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("Operation not found " + name);

        return null;
    }

    public Set<InstallOperation> getOperations(String stepName) {
        Set<InstallOperation> ops = operationsByStep.get(stepName);
        if (ops == null) {
            ops = new HashSet<InstallOperation>();
            operationsByStep.put(stepName, ops);
        }
        return ops;
    }

    public void register(String name, InstallOperation installOp) {
        operations.add(installOp);
        Set<InstallOperation> ops = operationsByStep.get(installOp.getStepName());
        if (ops == null) {
            ops = new HashSet<InstallOperation>(1);
            operationsByStep.put(installOp.getStepName(), ops);
        }
        ops.add(installOp);

        if (logger.isDebugEnabled())
            logger.debug("Registered operation " + name + " ("+installOp.getClass().getName()+") for step " + installOp.getStepName());

        installOp.init();
    }

    public void unregister(String name) {
        InstallOperation op = getOperation(name);
        if (op == null)
            return;

        op.shutdown();

        operations.remove(op);
        
        Set<InstallOperation> ops = operationsByStep.get(op.getStepName());
        if (ops != null)
            ops.remove(op);

        if (logger.isDebugEnabled())
            logger.debug("Unregistered operation " + name);

    }
}
