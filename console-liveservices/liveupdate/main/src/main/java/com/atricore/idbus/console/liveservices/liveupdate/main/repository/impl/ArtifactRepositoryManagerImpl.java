package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactNotFoundException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepository;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;

import java.io.InputStream;
import java.util.Collection;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ArtifactRepositoryManagerImpl extends AbstractRepositoryManager<ArtifactRepository>
        implements ArtifactRepositoryManager {

    /**
     * We need to work wit streams, in case artifact size is too big to store in RAM !!!
     */
    public InputStream getArtifactStream(ArtifactKeyType artifact) throws LiveUpdateException {

        for (ArtifactRepository repo : repos) {

            InputStream content = null;

            if (!repo.containsArtifact(artifact)) {
                try {
                    // TODO : Try to download it and store it in the repo
                    repo.getLocation();

                    // 1. Download descriptor

                    // 2. Validate it (include digital signature)

                    // 3. Download content (us streams to read/write, do not store in local byte[])

                } catch (Exception e) {
                    // Not found or error, try the next repository.
                    // TODO : logger !

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

    public void clearRepository(String repoName) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void clearAllRepositories() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addRepository(ArtifactRepository repo) throws LiveUpdateException {
        //To change body of created methods use File | Settings | File Templates.
    }
}
