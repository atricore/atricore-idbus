package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactsUtil;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DownloadInstallersOperation extends AbstractInstallOperation {

    private static final Log logger = LogFactory.getLog(DownloadInstallersOperation.class);

    // This is the artifact that should be used as installer
    private ArtifactRepositoryManager artMgr;

    private String systemFolder;

    public void init() {
        // This is where everything will be installed ...
        if (systemFolder == null)
            systemFolder = System.getProperty("karaf.base") + "/system";
    }

    @Override
    public OperationStatus preInstall(InstallEvent event) throws LiveUpdateException {

        for (InstallableUnitType iu : event.getContext().getIUs()) {
            for (ArtifactKeyType art : iu.getArtifact()) {

                if (art.getType().equals("bundle"))  {

                    // Input stream to read artifact from.
                    InputStream in = null;
                    // Input stream to read artifact from.
                    OutputStream out = null;

                    String artFileName = ArtifactsUtil.getArtifactFilePath(systemFolder, art) ;
                    String artFolderName = ArtifactsUtil.getArtifactFolderPath(systemFolder, art);

                    try {

                        in = artMgr.getArtifactStream(art);

                        File artFolder = new File(artFolderName);
                        if (!artFolder.exists()) {
                            artFolder.mkdirs();
                        }

                        File artFile = new File(artFileName);
                        out = new FileOutputStream(artFile, false);

                        // Installers are not zipped, so just copy them as they are.
                        IOUtils.copy(in, out);

                        if (logger.isDebugEnabled())
                            logger.debug("Downloaded installer : " + ArtifactsUtil.getArtifactFileName(art) + " ==> " +
                                    artFile.getAbsolutePath());

                    } catch (Exception e) {
                        logger.error("Can't install artifact " + artFileName + " : " + e.getMessage(), e);
                        return OperationStatus.STOP;

                    } finally {
                        IOUtils.closeQuietly(in);
                        IOUtils.closeQuietly(out);
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

    public String getSystemFolder() {
        return systemFolder;
    }

    public void setSystemFolder(String systemFolder) {
        this.systemFolder = systemFolder;
    }

    //------------------------------------------------------< Utilities >

}
