package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class RestartRequestOperation extends AbstractInstallOperation {

    @Override
    public OperationStatus postInstall(InstallEvent event) throws LiveUpdateException {
        // TODO : Pause until proper bundle is installed, the return NEXT :)
        // TODO : if (isUpdateInstalled) return OperationStatus.NEXT

        return OperationStatus.PAUSE;
    }
}