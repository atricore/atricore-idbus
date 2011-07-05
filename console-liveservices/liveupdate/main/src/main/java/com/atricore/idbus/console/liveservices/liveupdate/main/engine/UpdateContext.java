package com.atricore.idbus.console.liveservices.liveupdate.main.engine;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface UpdateContext {

    String getProcessId();

    UpdatePlan getPlan();

    Collection<InstallableUnitType> getIUs();

    ProfileType getProfile();

}
