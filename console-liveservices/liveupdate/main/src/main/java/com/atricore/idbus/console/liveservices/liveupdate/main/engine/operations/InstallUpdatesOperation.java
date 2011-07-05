package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactsUtil;
import com.atricore.idbus.console.liveservices.liveupdate.main.util.CompressionUtils;
import com.atricore.idbus.console.liveservices.liveupdate.main.util.FilePathUtil;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class InstallUpdatesOperation extends AbstractInstallOperation {

    private static final Log logger = LogFactory.getLog(InstallUpdatesOperation.class);

    // This is the artifact manager that should be used as installer
    private ArtifactRepositoryManager artMgr;

    private String unpackFolder;

    public void init() {
        // This is where everything will be unpacked ...

        if (unpackFolder == null)
            unpackFolder = System.getProperty("karaf.data",
                    System.getProperty("java.io.tmpdir")) +
                    "/liveservices/liveupdate/unpack";

        if (logger.isDebugEnabled())
            logger.debug("Using unpackFolder : " + unpackFolder);

        unpackFolder = FilePathUtil.fixFilePath(unpackFolder);

        try {
            File f = new File(new URI(unpackFolder));
            if (!f.exists()) {
                f.mkdirs();
            }
        } catch (URISyntaxException e) {
            logger.debug("Invalid unpack folder : " + e.getMessage(), e);
        }
    }

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        for (InstallableUnitType iu : event.getContext().getIUs()) {
            for (ArtifactKeyType art : iu.getArtifact()) {

                if ((SystemUtils.IS_OS_WINDOWS && art.getType().equals("zip")) ||
                        (!SystemUtils.IS_OS_WINDOWS && art.getType().equals("tar.gz"))) {

                    // Input stream to read artifact from.
                    InputStream in = null;

                    try {

                        in = artMgr.getArtifactStream(art);

                        String artifactFileName = ArtifactsUtil.getArtifactFileName(art);

                        if (logger.isDebugEnabled())
                            logger.debug("Downloaded artifact : " + artifactFileName);

                        // extract artifact
                        File unpackedDir = null;
                        if (art.getType().equals("zip")) {
                            unpackedDir = CompressionUtils.unpackZipFile(in, unpackFolder);
                        } else if (art.getType().equals("tar.gz")) {
                            unpackedDir = CompressionUtils.unpackTarGzFile(in, unpackFolder);
                        }

                        // copy extracted files
                        if (unpackedDir != null) {
                            FileUtils.copyDirectory(unpackedDir, new File(new URI(FilePathUtil.fixFilePath(System.getProperty("karaf.base")))));
                        }

                    } catch (Exception e) {
                        logger.error("Error unpacking artifact " + ArtifactsUtil.getArtifactFileName(art) + " : " + e.getMessage(), e);
                        return OperationStatus.STOP;

                    } finally {
                        IOUtils.closeQuietly(in);
                    }
                }
            }
        }

        return OperationStatus.NEXT;
    }

    public ArtifactRepositoryManager getArtMgr() {
        return artMgr;
    }

    public void setArtMgr(ArtifactRepositoryManager artMgr) {
        this.artMgr = artMgr;
    }

    public String getUnpackFolder() {
        return unpackFolder;
    }

    public void setUnpackFolder(String unpackFolder) {
        this.unpackFolder = unpackFolder;
    }
}
