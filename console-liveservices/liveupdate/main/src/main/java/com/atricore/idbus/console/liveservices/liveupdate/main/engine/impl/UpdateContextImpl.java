package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdatePlan;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateContextImpl implements UpdateContext {

    private String processId;

    private UpdatePlan updatePlan;

    private ProfileType updateProfile;

    public UpdateContextImpl(String processId, UpdatePlan plan , ProfileType updateProfile) {
        this.processId = processId;
        this.updatePlan = plan;
        this.updateProfile = updateProfile;
    }

    public String getProcessId() {
        return processId;
    }

    public UpdatePlan getPlan() {
        return updatePlan;
    }

    public Collection<InstallableUnitType> getIUs() {
        return updateProfile.getInstallableUnit();
    }

    public ProfileType getProfile() {
        return updateProfile;
    }
}
