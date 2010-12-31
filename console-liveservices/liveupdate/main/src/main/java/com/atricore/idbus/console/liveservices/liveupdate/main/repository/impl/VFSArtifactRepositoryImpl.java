package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations.ArtifactsUtil;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepository;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.Selectors;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;

import java.io.InputStream;
import java.io.OutputStream;
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
        artifacts.clear();
        loadArtifacts(repo);
    }

    public Collection<ArtifactDescriptorType> getAvailableArtifacts() {
        return artifacts.values();
    }

    public boolean containsArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException {
        return artifacts.containsKey(artifactKey.getID());
    }

    public InputStream getArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException {
        try {
            String artifactPath = ArtifactsUtil.getArtifactFilePath(artifactKey);
            FileObject artifact = repo.resolveFile(artifactPath);
            return artifact.getContent().getInputStream();

        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }

    public InputStream getArtifactDescriptor(ArtifactKeyType artifactKey) throws LiveUpdateException {
        try {
            String artifactDescriptorPath = ArtifactsUtil.getArtifactDescriptorPath(artifactKey);
            FileObject artifactDescriptor = repo.resolveFile(artifactDescriptorPath);
            return artifactDescriptor.getContent().getInputStream();

        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }

    public void addArtifact(ArtifactKeyType artifactKey, InputStream artifactStream, InputStream artifactDescriptorStream) throws LiveUpdateException {
        try {
            // store artifact
            FileObject artFile = repo.resolveFile(ArtifactsUtil.getArtifactFilePath(artifactKey));
            OutputStream artOut = artFile.getContent().getOutputStream();
            IOUtils.copy(artifactStream, artOut);
            artOut.flush();
            artOut.close();

            // store artifact descriptor
            FileObject artDescriptorFile = repo.resolveFile(ArtifactsUtil.getArtifactDescriptorPath(artifactKey));
            OutputStream artDescriptorOut = artDescriptorFile.getContent().getOutputStream();
            IOUtils.copy(artifactDescriptorStream, artDescriptorOut);
            artDescriptorOut.flush();
            artDescriptorOut.close();

            // add to hash map
            ArtifactDescriptorType artifactDescriptor = XmlUtils1.unmarshallArtifactDescriptor(getArtifactDescriptor(artifactKey), false);
            artifacts.put(artifactDescriptor.getArtifact().getID(), artifactDescriptor);
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }
    }

    public void removeArtifact(ArtifactKeyType artifactKey) throws LiveUpdateException {
        try {
            String artifactPath = ArtifactsUtil.getArtifactFilePath(artifactKey);
            FileObject artifact = repo.resolveFile(artifactPath);

            // remove from filesystem
            FileObject parent = artifact.getParent();
            while (!repo.toString().equals(parent.getParent().toString()) &&
                parent.getParent().getChildren().length == 1) {
                parent = parent.getParent();
            }
            parent.delete(Selectors.SELECT_ALL);

            // remove from hash map
            artifacts.remove(artifactKey.getID());
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
