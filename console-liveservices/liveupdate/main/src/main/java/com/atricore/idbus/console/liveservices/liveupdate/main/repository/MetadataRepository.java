package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateNatureType;

import java.util.Collection;

/**
 * Represents a server where update metadata can be obtained.
 * It also stores MD information locally.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela<.a>
 */
public interface MetadataRepository extends Repository<UpdateDescriptorType> {

    Collection<UpdateDescriptorType> getAvailableUpdates(Collection<InstallableUnitType> uis);

    Collection<UpdateDescriptorType> getAvailableUpdates(Collection<InstallableUnitType> uis, Collection<UpdateNatureType> updateNatures);

    Collection<UpdateDescriptorType> getAvailableUpdatesSince(java.util.Date from, Collection<UpdateNatureType> updateNatures);

    Collection<UpdateDescriptorType> getAvailableUpdates();

    void addUpdate(UpdateDescriptorType update);

    void removeUpdate(String id);

}
