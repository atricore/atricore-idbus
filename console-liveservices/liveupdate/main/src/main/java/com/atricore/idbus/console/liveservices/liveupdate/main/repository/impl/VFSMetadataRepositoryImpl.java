package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.MetadataRepository;
import com.atricore.liveservices.liveupdate._1_0.md.UpdateDescriptorType;
import com.atricore.liveservices.liveupdate._1_0.md.UpdatesIndexType;
import com.atricore.liveservices.liveupdate._1_0.util.XmlUtils1;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.*;

/**
 * File system backed MD Repository impl.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class VFSMetadataRepositoryImpl extends AbstractRepository<UpdateDescriptorType> implements MetadataRepository {

    private static final Log logger = LogFactory.getLog(VFSMetadataRepositoryImpl.class);

    private Map<String, UpdateDescriptorType> updates = new HashMap<String, UpdateDescriptorType>();

    private URI repoUri;

    private FileObject repo;

    private FileSystemManager fsManager;

    public VFSMetadataRepositoryImpl() {

    }

    public void init() throws LiveUpdateException {

        try {

            repo = getFileSystemManager().resolveFile(repoUri.toString());

            if (logger.isDebugEnabled())
                logger.debug("Initializing VFS MD Repository at " + repoUri.toString());

            if (!repo.exists())
                repo.createFolder();

            if (!repo.getType().getName().equals(FileType.FOLDER.getName()))
                throw new LiveUpdateException("Repository is not a folder : " + repo.getURL());

            if (!repo.isReadable())
                throw new LiveUpdateException("Repository is not readable : " + repo.getURL());

            if (!repo.isWriteable())
                throw new LiveUpdateException("Repository is not writeable : " + repo.getURL());

            List<UpdateDescriptorType> descrs = loadDescriptors();
            for (UpdateDescriptorType descr : descrs) {
                updates.put(descr.getID(), descr);
            }

        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        }

    }

    public Collection<UpdateDescriptorType> getAvailableUpdates() {
        return updates.values();
    }

    public void clear() throws LiveUpdateException {
        try {
            repo.delete(Selectors.EXCLUDE_SELF);
            updates.clear();
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        }
    }

    public void addUpdatesIndex(UpdatesIndexType newUpdates) throws LiveUpdateException {
        try {

            String updateStr = XmlUtils1.marshalUpdatesIndex(newUpdates, false);
            byte[] updateBin  = updateStr.getBytes();

            FileObject updateFile = repo.resolveFile(newUpdates.getID() + ".liveupdate");
            if (!updateFile.exists())
                updateFile.createFile();

            writeContent(updateFile, updateBin, false);

            for (UpdateDescriptorType ud : newUpdates.getUpdateDescriptor()) {
                updates.put(ud.getID(), ud);
            }
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }


    }

    public void removeUpdate(String id) {
        // Delete an update stored in this repo.
        updates.remove(id);
        // TODO: remove update descriptor element from update file?
    }

    public boolean hasUpdate(String id) {
        return this.updates.containsKey(id);
    }

    public URI getRepoFolder() {
        return repoUri;
    }

    public void setRepoFolder(URI repoFolder) {
        this.repoUri = repoFolder;
    }

    // ------------------------------< Utilities >

    protected List<UpdateDescriptorType> loadDescriptors() throws LiveUpdateException {

        List<UpdateDescriptorType> descrs = new ArrayList<UpdateDescriptorType>();
        try {
            for (FileObject f : repo.getChildren()) {
                byte[] descrBin = readContent(f);
                UpdatesIndexType idx = XmlUtils1.unmarshallUpdatesIndex(new String(descrBin), false);
                descrs.addAll(idx.getUpdateDescriptor());
            }
        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        } catch (Exception e) {
            throw new LiveUpdateException(e);
        }

        return descrs;
    }

    protected byte[] readContent(FileObject file) throws Exception {
        InputStream is = null;

        try {
            is = file.getContent().getInputStream();
            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = is.read(buf);
            while (read > 0) {
                baos.write(buf, 0, read);
                read = is.read(buf);
            }

            return baos.toByteArray();

        } finally {
            if (is != null) try {
                is.close();
            } catch (IOException e) { /**/}
        }

    }

    protected void writeContent(FileObject file, byte[] content, boolean append) throws Exception {
        OutputStream os = null;

        try {
            os = file.getContent().getOutputStream(append);
            os.write(content);

        } finally {
            if (os != null) try {
                os.close();
            } catch (IOException e) { /**/}
        }

    }
    protected FileSystemManager getFileSystemManager() {
        if (fsManager == null) {
            try {
                fsManager = VFS.getManager();
            } catch (FileSystemException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return fsManager;
    }

}
