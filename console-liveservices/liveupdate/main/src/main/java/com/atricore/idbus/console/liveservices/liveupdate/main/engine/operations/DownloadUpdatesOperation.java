package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactRepositoryManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.repository.ArtifactsUtil;
import com.atricore.liveservices.liveupdate._1_0.md.ArtifactKeyType;
import com.atricore.liveservices.liveupdate._1_0.md.InstallableUnitType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DownloadUpdatesOperation extends AbstractInstallOperation {

    private static final Log logger = LogFactory.getLog(DownloadUpdatesOperation.class);

    // This is the artifact manager that should be used as installer
    private ArtifactRepositoryManager artMgr;

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

                        if (logger.isDebugEnabled())
                            logger.debug("Downloaded artifact : " + ArtifactsUtil.getArtifactFileName(art));

                    } catch (Exception e) {
                        logger.error("Can't download artifact " + ArtifactsUtil.getArtifactFileName(art) + " : " + e.getMessage(), e);
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
}