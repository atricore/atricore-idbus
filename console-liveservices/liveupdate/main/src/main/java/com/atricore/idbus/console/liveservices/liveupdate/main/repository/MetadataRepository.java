package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;

import java.util.Collection;

/**
 * Represents a server where update metadata can be obtained.
 * It also stores MD information locally.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela<.a>
 */
public interface MetadataRepository extends Repository<UpdateDescriptorType> {

    /**
     * Get the list of Update descriptors stored in this repo.
     */
    Collection<UpdateDescriptorType> getAvailableUpdates();

    /**
     * Adds or replaces an updates index descriptor.
     */
    void addUpdatesIndex(UpdatesIndexType updates) throws LiveUpdateException;

    /**
     * True if an update descriptor exists with the given ID.
     */
    boolean hasUpdate(String id);

    /**
     * Retunrs the updates index descriptor.
     */
    UpdatesIndexType getUpdates();
}
