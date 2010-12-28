package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl.VFSMetadataRepositoryImpl;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;

import java.util.Collection;

/**
 * Manages a set of LiveUpdate MD repositories.
 *
 * It retrieves MD information for actual update services and stores it in the local repository representation.
 * Different transports are supported
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface MetadataRepositoryManager extends RepositoryManager {

    /**
     * Refresh all repositories by contacting repository servers.
     * @return the list of new updates found.
     */
    Collection<UpdateDescriptorType> refreshRepositories();

    /**
     * Retrieves an updates index descriptor for a given repository
     */
    UpdatesIndexType getUpdatesIndex(String repoName) throws LiveUpdateException;

    /**
     * Retrieves the entire list of available updates.
     */
    Collection<UpdateDescriptorType> getUpdates() throws LiveUpdateException;

    /**
     * Retrieves an update descriptor based on its ID
     */
    UpdateDescriptorType getUpdate(String id) throws LiveUpdateException;

    /**
     * Retrieves an update descriptor based on the IU group, name and version
     */
    UpdateDescriptorType getUpdate(String group, String name, String version) throws LiveUpdateException;

    /**
     * Adds a new MD repository to this manager
     */
    void addRepository(MetadataRepository repo) throws LiveUpdateException;
}
