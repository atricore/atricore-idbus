package com.atricore.idbus.console.liveservices.liveupdate.main;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.profile.ProfileType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface LiveUpdateManager {

    /**
     * Refresh MD repositories and returns a list of updates that apply to current profile
     */
    Collection<UpdateDescriptorType> checkForUpdates() throws LiveUpdateException;

    /**
     * Refresh MD repositories and returns a list of updates that apply to current profile
     */
    Collection<UpdateDescriptorType> checkForUpdates(String iuFqn) throws LiveUpdateException;


    /**
     * Returns a list of all updates available
     */
    Collection<UpdateDescriptorType> getAvailableUpdates() throws LiveUpdateException;

    /**
     * Returns a list of all updates available
     */
    Collection<UpdateDescriptorType> getAvailableUpdates(String iuFqn) throws LiveUpdateException;


    /**
     * Applies the given update to the current setup.
     */
    void applyUpdate(String group, String name, String version) throws LiveUpdateException;

    /**
     * Returns a profile with all the installable units that can be applied to the given setup.
     */
    ProfileType getUpdateProfile() throws LiveUpdateException;

    /**
     * Returns a profile with all the installable units required to install the given update in the current setup.
     */
    ProfileType getUpdateProfile(String group, String name, String version) throws LiveUpdateException;

    /**
     * Returns the profile representing the current setup.
     */
    ProfileType getCurrentProfile(boolean rebuild) throws LiveUpdateException;

    /**
     * Returns a list of configured repositories, the collection includes both MD and Artifact repositories.
     */
    Collection<Repository> getRepositories();

    /**
     * Returns a collection of ALL updates registered in a given repository.
     */
    UpdatesIndexType getRepositoryUpdates(String repoId) throws LiveUpdateException;

    /**
     * Clear repository local information
     */
    void cleanRepository(String repoId) throws LiveUpdateException;

    /**
     * Clear all repositories local information
     */
    void cleanAllRepositories() throws LiveUpdateException;

}
