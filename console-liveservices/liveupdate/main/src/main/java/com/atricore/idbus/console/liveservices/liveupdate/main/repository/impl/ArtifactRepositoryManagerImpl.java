package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ArtifactRepositoryManagerImpl extends AbstractRepositoryManager<ArtifactRepository>
        implements ArtifactRepositoryManager {

    public byte[] getArtifact(ArtifactKeyType artifact) {
        return new byte[0];
    }

    public void refreshRepositories() {

    }

    public Collection<ArtifactKeyType> getAvailableArtifacts() {
        return null;
    }
}
