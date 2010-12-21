package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;

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

    Collection<UpdateDescriptorType> refreshRepositories();

    Collection<UpdateDescriptorType> getAvailableUpdates();

}
