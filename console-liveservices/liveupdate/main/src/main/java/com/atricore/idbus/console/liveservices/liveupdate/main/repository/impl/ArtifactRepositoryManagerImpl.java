package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactsUtil;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.*;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ArtifactRepositoryManagerImpl extends AbstractRepositoryManager<ArtifactRepository>
        implements ArtifactRepositoryManager {

    private static final Log logger = LogFactory.getLog(ArtifactRepositoryManagerImpl.class);

    public void init() {
        // RFU
    }
    
    /**
     * We need to work wit streams, in case artifact size is too big to store in RAM !!!
     */
    public InputStream getArtifactStream(ArtifactKeyType artifact) throws LiveUpdateException {

        for (ArtifactRepository repo : repos) {

            if (!repo.isEnabled()) {
                if (logger.isDebugEnabled())
                    logger.debug("Ignoring disabled repository " + repo.getId());

                continue;
            }

            InputStream content = null;

            if (!repo.containsArtifact(artifact)) {
                try {
                    URI location = repo.getLocation();

                    for (RepositoryTransport t : transports) {

                        if (t.canHandle(location)) {

                            // download artifact descriptor
                            InputStream artifactDescriptorStream = t.getContentStream(new URI(location + "/" + ArtifactsUtil.getArtifactDescriptorPath(artifact)));

                            ArtifactDescriptorType artifactDescriptor = XmlUtils1.unmarshallArtifactDescriptor(artifactDescriptorStream, false);

                            if (logger.isTraceEnabled())
                                logger.trace("Found Artifact descriptor for artifact [" + artifact.getID() + "]");

                            // Validate Digital signature
                            if (repo.isSignatureValidationEnabled()) {
                                if (repo.getCertificate() != null) {
                                    LiveUpdateKeyResolver keyResolver = new LiveUpdateKeyResolverImpl(repo.getCertificate());
                                    try {
                                        liveUpdateSigner.validate(artifactDescriptor, keyResolver);
                                    } catch (InvalidSignatureException e) {
                                        logger.error("Signature is not valid for artifact descriptor [" + artifactDescriptor.getArtifact().getID() + "]. " +
                                                "Artifact [" + artifact.getID() + "] will not be downloaded.");
                                        return null;
                                    }
                                } else {
                                    logger.error("Signature validation is enabled but there is no valid certificate " +
                                            "for repository [" + repo.getId() + "]. Skipping artifact download.");
                                    return null;
                                }
                            }

                            // download artifact content and store it in the repo
                            repo.addArtifact(artifact,
                                    t.getContentStream(new URI(location + "/" + ArtifactsUtil.getArtifactFilePath(artifact))),
                                    t.getContentStream(new URI(location + "/" + ArtifactsUtil.getArtifactDescriptorPath(artifact))));

                            content = repo.getArtifact(artifact);
                        }
                        
                    }

                } catch (Exception e) {
                    // Not found or error, try the next repository.
                    logger.error("Error downloading or storing artifact [" + artifact.getID() + "] " +
                            "from repository [" + repo.getId() + "] " + e.getMessage(), e);
                }
            } else {
                content = repo.getArtifact(artifact);
            }

            if (content != null)
                return content;
        }

        // TODO : Add type and classifier, if anny.
        throw new ArtifactNotFoundException(artifact);
    }

    public void clearRepositories() throws LiveUpdateException {
        for (ArtifactRepository repo : repos) {
            if (repo.isEnabled()) {
                repo.clear();
            }
        }
    }

    public void clearRepository(String repoId) throws LiveUpdateException {
        for (ArtifactRepository repo : repos) {
            if (repo.isEnabled() && repo.getId().equals(repoId)) {
                repo.clear();
            }
        }
    }

    public void addRepository(ArtifactRepository repo) throws LiveUpdateException {
        repo.init();
        repos.add(repo);
    }
}
