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
     * Current execution profile, this is the cached version of it.
     */
    ProfileType getCurrentProfile() throws LiveUpdateException;

    /**
     * Calculates current execution profile.
     */
    ProfileType getCurrentProfile(boolean rebuild) throws LiveUpdateException;

    /**
     * Builds the profile containing all the necessary updates to install the provided IU in the current setup
     */
    ProfileType buildUpdateProfile(InstallableUnitType installable, Collection<UpdateDescriptorType> updates) throws LiveUpdateException;

    /**
     * Gets the list of updates that can be applied to the given installable unit
     */
    Collection<UpdateDescriptorType> getUpdates(InstallableUnitType updatable, Collection<UpdateDescriptorType> updates);
}
