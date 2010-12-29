package com.atricore.idbus.console.liveservices.liveupdate.admin.command;

import com.atricore.idbus.console.liveservices.liveupdate.admin.service.LiveUpdateAdminService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */

public abstract class LiveUpdateAdminCommandSupport extends OsgiCommandSupport {

    private static Log logger = LogFactory.getLog(LiveUpdateAdminCommandSupport.class);

    @Option(name = "-v", aliases = "--verbose", description = "Print out additional information", required = false, multiValued = false)
    boolean verbose = false;
    
    private LiveUpdateAdminService liveUpdateAdminService;
    
    @Override
    protected Object doExecute() throws Exception {
        doExecute(liveUpdateAdminService);
        return null;
    }

    protected byte[] readContent(String path) throws Exception {
        FileSystemManager fs = VFS.getManager();
        FileObject file = fs.resolveFile("file://" + path);
        if (!file.exists())
            throw new FileNotFoundException(file.getURL().toExternalForm());

        final int BUFFER_SIZE = 2048;
        int count;
        InputStream is = file.getContent().getInputStream();
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        byte[] buff =  new byte[BUFFER_SIZE];

        while ((count = is.read(buff, 0, BUFFER_SIZE)) != -1) {
            bOut.write(buff, 0, count);
        }
        bOut.flush();

        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
                logger.info("Unable to close stream for " + file.getURL() + ". Error:" + e.getMessage());
                if (logger.isDebugEnabled())
                    logger.debug("Unable to close stream for " + file.getURL() + ". Error:" + e.getMessage(), e);
            }
        }

        byte[] content = bOut.toByteArray();
        bOut.close();
        return content;
    }

    protected void writeContent(String path, byte[] content, boolean replace) throws Exception {
        FileSystemManager fs = VFS.getManager();
        FileObject outputFile = fs.resolveFile("file://" + path);
        if (!outputFile.exists()) {
            outputFile.createFile();
        } else if (!replace) {
            throw new Exception("Output file already exists, use --replace option. " + outputFile.getURL().toExternalForm());
        }

        OutputStream os = null;
        try {
            FileContent fc = outputFile.getContent();
            os = fc.getOutputStream(false);
            os.write(content, 0, content.length);
            os.flush();
            os.close();
        } finally {
            if (os != null) try {
                os.close();
            } catch (Exception e) {
                logger.info("Unable to close stream for " + outputFile.getURL() + ". Error:" + e.getMessage());
                if (logger.isDebugEnabled())
                    logger.debug("Unable to close stream for " + outputFile.getURL() + ". Error:" + e.getMessage(), e);
            }
        }

    }

    protected abstract Object doExecute(LiveUpdateAdminService svc) throws Exception;

    public LiveUpdateAdminService getLiveUpdateAdminService() {
        return liveUpdateAdminService;
    }

    public void setLiveUpdateAdminService(LiveUpdateAdminService liveUpdateAdminService) {
        this.liveUpdateAdminService = liveUpdateAdminService;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
