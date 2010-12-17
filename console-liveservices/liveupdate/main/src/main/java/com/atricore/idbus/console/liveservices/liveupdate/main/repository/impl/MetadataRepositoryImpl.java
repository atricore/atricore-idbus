package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateNatureType;

import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * File system backed MD Repository impl.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class MetadataRepositoryImpl extends AbstractRepository<UpdateDescriptorType> implements MetadataRepository {

    private Map<String, UpdateDescriptorType> updates = new HashMap<String, UpdateDescriptorType>();

    private URI repoFolder;

    public MetadataRepositoryImpl() {

    }

    public void init() {


        // TODO : Load all updates from disk.
        // TODO : Setup repo folder based on repos base folder ( $KARAF_DATA/liveupdate/repos/<repo-ID> )
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates(Collection<InstallableUnitType> uis) {
        return null;
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates(Collection<InstallableUnitType> uis, Collection<UpdateNatureType> updateNatures) {
        return null;
    }

    public Collection<UpdateDescriptorType> getAvailableUpdatesSince(Date from, Collection<UpdateNatureType> updateNatures) {
        return null;
    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() {
        return null;
    }

    public void clear () {
        // Destroy all updates stored in this repo.
    }

    public void addUpdate(UpdateDescriptorType update) {
        // Store new update definition in repo.
    }

    public void removeUpdate(String id) {
        // Delete an update stored in this repo.
    }

    public URI getRepoFolder() {
        return repoFolder;
    }

    public void setRepoFolder(URI repoFolder) {
        this.repoFolder = repoFolder;
    }
}
