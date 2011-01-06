package com.atricore.idbus.console.liveservices.liveupdate.main.repository;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

import java.io.InputStream;
import java.util.Collection;

/**
 * Represents a server where artifacts can be obtained.
 * It also stores artifacts locally.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface ArtifactRepository extends Repository<ArtifactKeyType> {

    /**
     * Returns the list of locally stored artifacts
     */
    Collection<ArtifactDescriptorType> getAvailableArtifacts() throws LiveUpdateException;

    /**
     * Returns true if the artifact exists in the local storage
     */
    boolean containsArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException;

    /**
     * Gets the content for the given artifact
     */
    InputStream getArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException;

    /**
     * Gets the content for the given artifact descriptor
     */
    InputStream getArtifactDescriptor(ArtifactKeyType artifactKey) throws LiveUpdateException;

    /**
     * Adds an artifact to local storage
     */
    void addArtifact(ArtifactKeyType artifactKey, InputStream artifactStream, InputStream artifactDescriptorStream) throws LiveUpdateException;
    
    /**
     * Removes an artifact from local storage
     */
    void removeArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException;

    /**
     * Removes all artifacts from local storage
     * @throws LiveUpdateException
     */
    void clear() throws LiveUpdateException;
}
