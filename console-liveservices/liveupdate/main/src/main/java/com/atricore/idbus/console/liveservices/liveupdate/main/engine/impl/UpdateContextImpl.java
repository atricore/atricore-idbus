package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdateContextImpl implements UpdateContext {

    private ProfileType updateProfile;

    public UpdateContextImpl(ProfileType updateProfile) {
        this.updateProfile = updateProfile;
    }

    public Collection<InstallableUnitType> getIUs() {
        return updateProfile.getInstallableUnit();
    }
}
