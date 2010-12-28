package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;

import static com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations.ArtifactsUtil.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DownloadInstallersOperation extends AbstractInstallOperation {

    private static final Log logger = LogFactory.getLog(DownloadInstallersOperation.class);

    // This is the artifact that should be used as installer
    private ArtifactRepositoryManager artMgr;

    private String systemFolder;

    public void init() {
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

                    String artFileName = getArtifactFileName(systemFolder, art) ;
                    String artFolderName = getArtifactFolderName(systemFolder, art);

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

                    } catch (Exception e) {
                        logger.error("Can't install artifact " + artFileName + " : " + e.getMessage(), e);
                        return OperationStatus.STOP;

                    } finally {
                        if (in != null) try {in.close(); } catch (IOException e) { logger.debug(e.getMessage(), e); }
                        if (out != null) try {out.close(); } catch (IOException e) { logger.debug(e.getMessage(), e); }
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
