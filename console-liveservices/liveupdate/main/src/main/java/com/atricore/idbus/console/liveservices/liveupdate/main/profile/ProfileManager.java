package com.atricore.idbus.console.liveservices.liveupdate.main.profile;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdatePlan;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;


import java.util.Collection;
import java.util.List;

/**
 * Keeps track of configurations or profiles.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ProfileManager {

    /**
     * Current executing profile, this is the cached version of it.
     */
    ProfileType getCurrentProfile() throws LiveUpdateException;

    /**
     * Calculates current execution profile.
     */
    ProfileType buildCurrentProfile() throws LiveUpdateException;

    /**
     * Builds the profile containing all the necessary updates to install the provided IU in the current setup
     */
    ProfileType buildUpdateProfile(UpdateDescriptorType install, Collection<UpdateDescriptorType> updates) throws LiveUpdateException;

}
