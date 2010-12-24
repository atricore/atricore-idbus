package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.Repository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractVFSRepository<T> implements Repository<T> {

    private static final Log logger = LogFactory.getLog(AbstractVFSRepository.class);

    private String id;

    private String name;

    private String publicKey;

    private boolean isEnabled;

    private String username;

    private String password;

    private URI location;

    private URI repoUri;

    protected FileObject repo;

    protected FileSystemManager fsManager;

    public void init() throws LiveUpdateException {
        try {

            repo = getFileSystemManager().resolveFile(repoUri.toString());

            if (logger.isDebugEnabled())
                logger.debug("Initializing VFS Repository at " + repoUri.toString());

            if (!repo.exists())
                repo.createFolder();

            if (!repo.getType().getName().equals(FileType.FOLDER.getName()))
                throw new LiveUpdateException("Repository is not a folder : " + repo.getURL());

            if (!repo.isReadable())
                throw new LiveUpdateException("Repository is not readable : " + repo.getURL());

            if (!repo.isWriteable())
                throw new LiveUpdateException("Repository is not writeable : " + repo.getURL());

        } catch (FileSystemException e) {
            throw new LiveUpdateException(e);
        }
    }

    public void clear() throws LiveUpdateException {

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

    // ------------------------------< Utilities >
    
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

    // ------------------------------< Getters/Setters >

    public URI getRepoFolder() {
        return repoUri;
    }

    public void setRepoFolder(URI repoFolder) {
        this.repoUri = repoFolder;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public URI getLocation() {
        return location;
    }

    public void setLocation(URI location) {
        this.location = location;
    }
}
