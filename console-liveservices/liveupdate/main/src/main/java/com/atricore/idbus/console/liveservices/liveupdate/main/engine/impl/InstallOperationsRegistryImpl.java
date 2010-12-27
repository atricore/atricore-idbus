package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallOperationsRegistry;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InstallOperationsRegistryImpl implements InstallOperationsRegistry {

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
    }

    public void unregister(String name) {
        // TODO :
    }
}
