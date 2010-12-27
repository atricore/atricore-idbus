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

    void checkForUpdates();

    Collection<Repository> getRepositories();

    ProfileType getCurrentProfile() throws LiveUpdateException;

    UpdatesIndexType getRepositoryUpdates(String repoName) throws LiveUpdateException;

    Collection<UpdateDescriptorType> getAvailableUpdates() throws LiveUpdateException;
}
