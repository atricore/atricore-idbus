package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateNatureType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;

import java.util.Collection;

/**
 * Represents a server where update metadata can be obtained.
 * It also stores MD information locally.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela<.a>
 */
public interface MetadataRepository extends Repository<UpdateDescriptorType> {

    Collection<UpdateDescriptorType> getAvailableUpdates();

    void addUpdatesIndex(UpdatesIndexType updates) throws LiveUpdateException;

    void removeUpdate(String id);

    boolean hasUpdate(String id);
}
