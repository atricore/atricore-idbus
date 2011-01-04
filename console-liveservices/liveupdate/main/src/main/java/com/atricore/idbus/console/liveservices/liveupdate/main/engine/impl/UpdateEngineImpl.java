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
            procs.put(p.getId(), p);
            this.seekProcess(p, state.getOperation());
        }
    }

    public void execute(String planName, ProfileType updateProfile) throws LiveUpdateException {

        UpdatePlan plan = getPlan(planName);


        String procId = uuidGen.generateId();

        if (logger.isDebugEnabled())
            logger.debug("Starting update plan : " + plan.getName() + " in process " + procId);

        UpdateProcess proc = startProcess(plan, updateProfile);

        OperationStatus sts = advanceProcess(proc);
        while(sts.equals(OperationStatus.NEXT))
            sts = advanceProcess(proc);

        if (sts.equals(OperationStatus.STOP)) {
            if (logger.isDebugEnabled())
                logger.debug("Process completed " + proc.getId());
            store.remove(proc.getId());
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

    protected UpdateProcess startProcess(UpdatePlan plan, ProfileType updateProfile) throws LiveUpdateException {

        String procId = uuidGen.generateId();

        if (logger.isDebugEnabled())
            logger.debug("Starting update plan : " + plan.getName() + " in process " + procId);

        UpdateContext ctx = new UpdateContextImpl(procId, plan, updateProfile);

        UpdateProcess proc = new UpdateProcess(getOperationsRegistry(), ctx);
        procs.put(proc.getId(), proc);

        proc.init();

        store.save(proc.getState());

        // TODO : Persist process information (use properties or osgi cfg ?!)
        return proc;

    }

    protected OperationStatus advanceProcess(String processId) throws LiveUpdateException {
        UpdateProcess proc = procs.get(processId);
        if (proc == null)
            throw new LiveUpdateException("Cannot find update process " + processId);
        return advanceProcess(proc);
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
