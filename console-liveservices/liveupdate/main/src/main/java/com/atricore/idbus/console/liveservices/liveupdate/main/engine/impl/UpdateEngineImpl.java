package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.*;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateEngineImpl implements UpdateEngine {

    private static Log logger = LogFactory.getLog(UpdateEngineImpl.class);

    private Set<UpdatePlan> plans;

    private InstallOperationsRegistry operationsRegistry;

    public void init() throws LiveUpdateException {
        // TODO : Resume stalled stalled processes!
    }

    // TODO : Provide persistence functionallity to resume an update process after reboot
    public void execute(String planName, ProfileType updateProfile) throws LiveUpdateException {

        UpdateContext ctx = new UpdateContextImpl(updateProfile);
        for (UpdatePlan plan : plans) {
            if (plan.getName().equals(planName))
                start(plan, ctx);
        }
    }

    protected String start(UpdatePlan plan, UpdateContext ctx) {

        // Startup process for given plan, using provided context, return process id
        return null;

    }

    protected void execute(String id) {

        UpdatePlan plan = null;
        UpdateContext ctx = null;
        try {
            for (Step step : plan.getSteps()) {
                Set<InstallOperation> operations = operationsRegistry.getOperations(step.getName());

                for (InstallOperation operation : operations) {
                    operation.preInstall(ctx);
                }

                for (InstallOperation operation : operations) {
                    operation.postInstall(ctx);
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
