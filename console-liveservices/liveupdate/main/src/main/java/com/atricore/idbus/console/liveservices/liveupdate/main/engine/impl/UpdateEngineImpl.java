package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.*;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateEngineImpl implements UpdateEngine {

    private static Log logger = LogFactory.getLog(UpdateEngineImpl.class);

    private static final UUIDGenerator uuidGen = new UUIDGenerator();

    private Set<UpdatePlan> plans = new HashSet<UpdatePlan>();

    private InstallOperationsRegistry operationsRegistry;

    private Map<String, UpdateProcess> procs = new HashMap<String, UpdateProcess>();

    private ProcessStore store;

    public void init() throws LiveUpdateException {

        if (logger.isDebugEnabled())
            logger.debug("Installed plans : " + plans.size());

        // TODO : Validate plans are unique, by name

        // TODO : Resume stalled processes!
        Collection<UpdateProcessState> saved = store.load();
        for (UpdateProcessState state : saved) {

            if (logger.isDebugEnabled())
                logger.debug("Restoring process " + state.getId());

            UpdatePlan plan =  getPlan(state.getPlan());
            UpdateContext ctx = new UpdateContextImpl(state.getId(), plan, state.getUpdateProfile());
            UpdateProcess p = new UpdateProcess(getOperationsRegistry(), ctx);
            p.init();
            procs.put(p.getId(), p);
            this.seekProcess(p, state.getOperation());
        }
    }

    public void execute(String planName, ProfileType updateProfile) throws LiveUpdateException {

        UpdatePlan plan = getPlan(planName);

        UpdateProcess proc = startProcess(plan, updateProfile);

        proc = executeProcess(proc);

        if (logger.isDebugEnabled())
            logger.debug("Process Executed : " + proc.getId());

    }

    public void resumeAll() throws LiveUpdateException {
        for (String procId : procs.keySet()) {
            UpdateProcess proc = resumeProcess(procId);
            executeProcess(proc);
            if (logger.isDebugEnabled())
                logger.debug("Process Executed (resume) : " + proc.getId());

        }
    }

    //------------------------------------------------------< Properties >

    public Set<UpdatePlan> getPlans() {
        return plans;
    }

    public void setPlans(Set<UpdatePlan> plans) {
        this.plans = plans;
    }

    public InstallOperationsRegistry getOperationsRegistry() {
        return operationsRegistry;
    }

    public void setOperationsRegistry(InstallOperationsRegistry operationsRegistry) {
        this.operationsRegistry = operationsRegistry;
    }

    public ProcessStore getStore() {
        return store;
    }

    public void setStore(ProcessStore store) {
        this.store = store;
    }

    //------------------------------------------------------< Utilities >

    protected UpdateProcess executeProcess(UpdateProcess proc) throws LiveUpdateException {

        proc.getState().setStatus(ProcessStatus.RUNNING);
        OperationStatus sts = advanceProcess(proc);

        while(sts.equals(OperationStatus.NEXT)) {
            if (logger.isTraceEnabled())
                logger.trace("Process advanced " + proc.getId());
            sts = advanceProcess(proc);
        }

        switch (sts) {
            case STOP:
                if (logger.isDebugEnabled())
                    logger.debug("Process completed " + proc.getId());
                stopProcess(proc);
               break;

            case PAUSE:
                if (logger.isDebugEnabled())
                    logger.debug("Process paused " + proc.getId());
                pauseProcess(proc);
                break;

            default:
            logger.error("Unknown operation status " + sts.name());
            stopProcess(proc);
        }

        return proc;

    }

    protected UpdateProcess startProcess(UpdatePlan plan, ProfileType updateProfile) throws LiveUpdateException {

        String procId = uuidGen.generateId();

        if (logger.isDebugEnabled())
            logger.debug("Starting process for : " + plan.getName() + ". ID : " + procId);

        UpdateContext ctx = new UpdateContextImpl(procId, plan, updateProfile);

        UpdateProcess proc = new UpdateProcess(getOperationsRegistry(), ctx);
        procs.put(proc.getId(), proc);

        proc.init();
        proc.getState().setStatus(ProcessStatus.STARTED);

        store.save(proc.getState());

        return proc;

    }


    protected UpdateProcess resumeProcess(String processId) throws LiveUpdateException {
        UpdateProcess proc = procs.get(processId);
        if (proc == null)
            throw new LiveUpdateException("Cannot find update process " + processId);

        if (logger.isDebugEnabled())
            logger.debug("Resuming process " + processId);

        return proc;
    }

    protected OperationStatus advanceProcess(String processId) throws LiveUpdateException {
        UpdateProcess proc = procs.get(processId);
        if (proc == null)
            throw new LiveUpdateException("Cannot find update process " + processId);
        return advanceProcess(proc);
    }

    protected UpdateProcess stopProcess(UpdateProcess proc) throws LiveUpdateException {
        proc.getState().setStatus(ProcessStatus.STOPPED);
        store.remove(proc.getId());
        return proc;
    }

    protected UpdateProcess pauseProcess(UpdateProcess proc) throws LiveUpdateException {
        proc.getState().setStatus(ProcessStatus.PAUSED);
        store.save(proc.getState());
        return proc;
    }


    protected OperationStatus advanceProcess(UpdateProcess proc) throws LiveUpdateException {

        InstallOperation op = proc.advance();

        // If no more operations are available, stop the process.
        if (op == null) {
            if (logger.isTraceEnabled())
                logger.trace("Process " + proc.getId() + " status (no more operations) : " + OperationStatus.STOP);

            return OperationStatus.STOP;
        }

        if (logger.isTraceEnabled())
            logger.trace("Process " + proc.getId() + " Step/Op:" +
                    proc.getCurrentStep().getName() + "/" + op.getName());

        InstallEvent event = new InstallEventImpl(proc.getCurrentStep(), proc.getCotenxt());
        try {
            OperationStatus sts = op.execute(event);
            if (logger.isTraceEnabled())
                logger.trace("Process " + proc.getId() + " status : " + sts.name());

            return sts;
        } finally {
            store.save(proc.getState());
        }

    }

    protected void seekProcess(UpdateProcess proc, String opName) {
        proc.seek(opName);
    }

    protected UpdatePlan getPlan (String planName) {
        for (UpdatePlan plan : plans) {
            if (plan.getName().equals(planName)) {
                return plan;
            }
        }

        return null;

    }

}
