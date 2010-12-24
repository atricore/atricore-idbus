package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

import java.util.Collection;

/**
 * Represents a server where artifacts can be obtained.
 * It also stores artifacts locally.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ArtifactRepository extends Repository<ArtifactKeyType> {

    Collection<ArtifactDescriptorType> getAvailableArtifacts();

    byte[] getArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException;

    void removeArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException;
}
