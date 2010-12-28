package com.atricore.idbus.console.liveservices.liveupdate.main.repository.impl;

import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransport;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.RepositoryTransportException;

import java.io.*;
import java.net.URI;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class FileRepositoryTransport implements RepositoryTransport {

    private String baseFolder;

    public boolean canHandle(URI uri) {
        return uri.getScheme() != null && uri.getScheme().equals("file");
    }

    public byte[] loadContent(URI uri) throws RepositoryTransportException {

        // validate that file belongs to baseFolder, if any
        if (baseFolder != null && !baseFolder.equals(uri.toString().substring(0, uri.toString().lastIndexOf("/")))) {
            throw new RepositoryTransportException("File doesn't belong to baseFolder.");
        }

        FileInputStream fs = null;
        try {
            File f = new File(uri);

            byte[] buf = new byte[1024];
            fs = new FileInputStream(f);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int read = fs.read(buf);
            while (read > 0) {
                baos.write(buf, 0, read);
                read = fs.read(buf);
            }

            return baos.toByteArray();

        } catch (FileNotFoundException e) {
            throw new RepositoryTransportException(e);
        } catch (IOException e) {
            throw new RepositoryTransportException(e);
        } finally {
            if (fs != null) try {fs.close();} catch (IOException e) { /*Ignore this*/ }
        }
    }

    public String getBaseFolder() {
        return baseFolder;
    }

    public void setBaseFolder(String baseFolder) {
        this.baseFolder = baseFolder;
        if (this.baseFolder.endsWith("/")) {
            this.baseFolder = this.baseFolder.substring(0, this.baseFolder.length() - 1);
        }
    }
}
