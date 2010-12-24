package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

import java.util.Collection;

/**
 * Manages a set of LiveUpdate Artifact repositories.
 *
 * It retrieves Artifacts for actual update services and stores it in the local repository representation.
 * Different transports are supported
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ArtifactRepositoryManager extends RepositoryManager {

    void refreshRepositories();

    Collection<ArtifactKeyType> getAvailableArtifacts();

    byte[] getArtifact(ArtifactKeyType artifact);
}
