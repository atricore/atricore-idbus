package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepository;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * File system backed Artifact Repository impl.
 * Stores artifacts using maven layout.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class VFSArtifactRepositoryImpl extends AbstractVFSRepository<ArtifactKeyType> implements ArtifactRepository {

    private static final Log logger = LogFactory.getLog(VFSArtifactRepositoryImpl.class);

    private Map<String, ArtifactKeyType> artifacts = new HashMap<String, ArtifactKeyType>();

    public VFSArtifactRepositoryImpl() {

    }

    public void init() throws LiveUpdateException {
        super.init();
        // load artifacts
        loadArtifacts(repo);
    }

    public Collection<ArtifactKeyType> getAvailableArtifacts() {
        return artifacts.values();
    }

    public byte[] getArtifact(ArtifactKeyType artifactKey) {
        return new byte[0];
    }

    protected void loadArtifacts(FileObject dir) throws LiveUpdateException {
        try {
            for (FileObject f : dir.getChildren()) {
                if (f.getType() == FileType.FOLDER) {
                    loadArtifacts(f);
                } else if (f.getType() == FileType.FILE) {
                    ArtifactKeyType artifactKey = new ArtifactKeyType();
                    artifactKey.setID("id" + artifacts.size() + 1);
                    String fullGroupPath = f.getName().getParent().getParent().getParent().toString();
                    artifactKey.setGroup(fullGroupPath.substring(repo.toString().length() + 1, fullGroupPath.length())
                            .replaceAll(File.separator, "."));
                    artifactKey.setName(f.getName().getParent().getParent().getBaseName());
                    artifactKey.setVersion(f.getName().getParent().getBaseName());
                    artifactKey.setType(f.getName().getExtension());
                    String classifier = f.getName().getBaseName().substring(
                            artifactKey.getName().length() + artifactKey.getVersion().length() + 1,
                            f.getName().getBaseName().length() - artifactKey.getType().length() - 1);
                    if (classifier.startsWith("-")) {
                        classifier = classifier.substring(1, classifier.length());
                    }
                    artifactKey.setClassifier(classifier);
                    artifacts.put(artifactKey.getID(), artifactKey);
                }
            }
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }
}
