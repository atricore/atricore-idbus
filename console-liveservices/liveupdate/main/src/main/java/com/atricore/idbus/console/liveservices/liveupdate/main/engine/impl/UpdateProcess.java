package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.*;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class UpdateProcess {

    private InstallOperationsRegistry reg;

    private UpdateContext ctx;

    private LinkedList<Step> steps = new LinkedList<Step>();

    private LinkedList<InstallOperation> operations = new LinkedList<InstallOperation>();

    private UpdateProcessState state;

    public UpdateProcess(InstallOperationsRegistry reg, UpdateContext ctx) {
        this.ctx = ctx;
        this.reg = reg;
        this.state = new UpdateProcessState();
        state.setId(ctx.getProcessId());
        state.setPlan(ctx.getPlan().getName());
        state.setUpdateProfile(ctx.getProfile());

    }

    public void init() {
        UpdatePlan p = ctx.getPlan();
        for (Step s : p.getSteps()) {
            steps.add(s);
        }

    }

    public String getId() {
        return ctx.getProcessId();
    }

    public UpdateContext getCotenxt() {
        return ctx;
    }

    public UpdateProcessState getState() {
        return state;
    }

    public InstallOperation seek(String operationName) {

        InstallOperation op = advance();
        while (op != null) {
            if (op.getName().equals(operationName)) {
                state.setOperation(op.getName());
                return op;
            }
            op = advance();
        }

        return null;
    }

    public InstallOperation advance() {
        while (operations.size() == 0 && steps.size() > 0) {
            prepareNextStep();
        }

        InstallOperation op = operations.poll();

        if (op != null) {
            state.setOperation(op.getName());
        } else {
            state.setOperation(null);
        }

        return op;

    }

    public Step getCurrentStep() {
        return steps.peek();
    }

    public InstallOperation getNextOperation() {

        int i = 0;
        Collection<InstallOperation> ops = operations;
        while (ops.size() == 0 && i < steps.size()) {
            Step s = steps.get(i);
            ops = reg.getOperations(s.getName());
        }
        if (ops.size() > 0)
            return ops.iterator().next();

        return null;
    }

    protected void prepareNextStep() {
        Step s = steps.poll();
        // No more steps
        if (s == null) {
            operations.clear();
            return;
        }

        // We want to get operations dynamically
        Collection<InstallOperation> ops = reg.getOperations(s.getName());
        for (InstallOperation op : ops) {
            operations.add(op);
        }

    }

}
