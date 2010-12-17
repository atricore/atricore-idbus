package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepositoryManager;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

import java.util.Collection;
import java.util.List;

/**
 * Manages a set of LiveUpdate MD repositories.
 *
 * It retrieves MD information for actual update services and stores it in the local repository representation.
 * Different transports are supported
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MetadataRepositoryManagerImpl extends AbstractRepositoryManager<MetadataRepository>
    implements MetadataRepositoryManager {

    private List<MetadataRepository> repos;

    public void init() {

    }

    public void checkForUpdates() {
        // Loop over configured repos
        // Contact remote service,
        // Retrieve update list,
        // Store it locally
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() {
        return null;
    }


}
