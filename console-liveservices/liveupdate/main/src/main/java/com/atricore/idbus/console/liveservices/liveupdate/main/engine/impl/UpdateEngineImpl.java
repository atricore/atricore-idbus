package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.*;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateEngineImpl implements UpdateEngine {

    private static Log logger = LogFactory.getLog(UpdateEngineImpl.class);

    private static final UUIDGenerator uuidGen = new UUIDGenerator();

    private Set<UpdatePlan> plans = new HashSet<UpdatePlan>();

    private InstallOperationsRegistry operationsRegistry;

    public void init() throws LiveUpdateException {

        if (logger.isDebugEnabled())
            logger.debug("Installed plans : " + plans.size());

        // TODO : Resume stalled processes!
    }

    // TODO : Provide persistence functionallity to resume an update process after reboot
    public void execute(String planName, ProfileType updateProfile) throws LiveUpdateException {

        for (UpdatePlan plan : plans) {

            if (plan.getName().equals(planName)) {

                String procId = uuidGen.generateId();

                if (logger.isDebugEnabled())
                    logger.debug("Starting update plan : " + plan.getName() + " in process " + procId);

                UpdateContext ctx = new UpdateContextImpl(procId, plan, updateProfile);

                execute(plan, ctx);
            }
        }
    }

    protected void execute(UpdatePlan plan , UpdateContext ctx) {

        if (logger.isTraceEnabled())
            logger.trace("currentPlan=>" + plan.getName());

        try {

            for (Step step : plan.getSteps()) {

                if (logger.isTraceEnabled())
                    logger.trace("currentStep=>" + step.getName());

                InstallEvent event = new InstallEventImpl(step, ctx);

                Set<InstallOperation> operations = operationsRegistry.getOperations(step.getName());

                for (InstallOperation operation : operations) {
                    if (logger.isTraceEnabled())
                        logger.trace("preInstall=>" + operation.getName());

                    OperationStatus sts = operation.preInstall(event);

                    if (sts.equals(OperationStatus.PAUSE)) {
                        // Pause process ... when will be resumed!?

                    } else if (sts.equals(OperationStatus.STOP)) {
                        // Stop process .. will not resume.
                    }
                }

                for (InstallOperation operation : operations) {
                    if (logger.isTraceEnabled())
                        logger.trace("postInstall=>" + operation.getName());
                    OperationStatus sts = operation.postInstall(event);

                    if (sts.equals(OperationStatus.PAUSE)) {
                        // Pause process ... when will be resumed!?

                    } else if (sts.equals(OperationStatus.STOP)) {
                        // Stop process .. will not resume.
                    }

                }

            }
        } catch (LiveUpdateException e) {
            logger.error(e.getMessage(), e);
        }
    }

    protected void resume(String processId) {
        // TODO !
    }

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



}
