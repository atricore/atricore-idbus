package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepository;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.Selectors;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

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

    private Map<String, ArtifactDescriptorType> artifacts = new HashMap<String, ArtifactDescriptorType>();

    protected UUIDGenerator idGen = new UUIDGenerator();
    
    public VFSArtifactRepositoryImpl() {

    }

    public void init() throws LiveUpdateException {
        super.init();
        // load artifacts
        loadArtifacts(repo);
    }

    public Collection<ArtifactDescriptorType> getAvailableArtifacts() {
        return artifacts.values();
    }

    public byte[] getArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException {
        try {
            String artifactPath = artifactKey.getGroup().replaceAll("\\.", File.separator) + File.separator +
                    artifactKey.getName() + File.separator +
                    artifactKey.getVersion() + File.separator +
                    artifactKey.getName() + "-" + artifactKey.getVersion() +
                    (artifactKey.getClassifier() != null && artifactKey.getClassifier() != "" ?
                            "-" + artifactKey.getClassifier() : "") + "." +
                    (artifactKey.getType() != null && artifactKey.getType() != "" ? artifactKey.getType() : "jar");
            FileObject artifact = repo.resolveFile(artifactPath);
            return readContent(artifact);
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }

    public void removeArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException {
        try {
            String artifactPath = artifactKey.getGroup().replaceAll("\\.", File.separator) + File.separator +
                    artifactKey.getName() + File.separator +
                    artifactKey.getVersion() + File.separator +
                    artifactKey.getName() + "-" + artifactKey.getVersion() +
                    (artifactKey.getClassifier() != null && artifactKey.getClassifier() != "" ? "-" : "") +
                    artifactKey.getClassifier() + "." +
                    (artifactKey.getType() != null && artifactKey.getType() != "" ? artifactKey.getType() : "jar");
            FileObject artifact = repo.resolveFile(artifactPath);
            // TODO
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }

    public void clear() throws LiveUpdateException {
        try {
            repo.delete(Selectors.EXCLUDE_SELF);
            artifacts.clear();
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        }
    }

    protected void loadArtifacts(FileObject dir) throws LiveUpdateException {
        try {
            for (FileObject f : dir.getChildren()) {
                if (f.getType() == FileType.FOLDER) {
                    loadArtifacts(f);
                } else if (f.getType() == FileType.FILE && f.getName().getExtension().equals("xml")) {
                    byte[] descrBin = readContent(f);
                    ArtifactDescriptorType artifactDescriptor = XmlUtils1.unmarshallArtifactDescriptor(new String(descrBin), false);
                    artifacts.put(artifactDescriptor.getArtifact().getID(), artifactDescriptor);
                }
            }
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }
}
