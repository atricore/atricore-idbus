package com.atricore.idbus.console.liveservices.liveupdate.main.profile;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;


import java.util.Collection;

/**
 * Keeps track of configurations or profiles.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ProfileManager {

    /**
     * Current executing profile
     */
    ProfileType getCurrentProfile() throws LiveUpdateException;

    /**
     * New profile, based on the list of updates.
     */
    ProfileType createProfile(ProfileType original, Collection<UpdateDescriptorType> updates);

}
