package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RestartRequestOperation extends AbstractInstallOperation {

    private LiveUpdateManager luMgr;

    @Override
    public OperationStatus postInstall(InstallEvent event) throws LiveUpdateException {
        // TODO : Pause until proper bundle is installed, the return NEXT :)
        // TODO : if (isUpdateInstalled) return OperationStatus.NEXT
        // luMgr.getCurrentProfile(true);

        return OperationStatus.PAUSE;
    }
}