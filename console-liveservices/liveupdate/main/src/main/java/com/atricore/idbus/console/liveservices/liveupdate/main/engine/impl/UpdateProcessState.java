package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.ProcessStatus;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

import java.io.Serializable;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class UpdateProcessState implements Serializable {

    private String id;

    private String plan;

    private String operation;

    private ProfileType updateProfile;

    private ProcessStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProfileType getUpdateProfile() {
        return updateProfile;
    }

    public void setUpdateProfile(ProfileType updateProfile) {
        this.updateProfile = updateProfile;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
}
