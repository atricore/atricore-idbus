package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class DownloadUpdatesOperation extends AbstractInstallOperation {

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        // TODO : Download and extract distro artifacts for all IUs
        return OperationStatus.NEXT;
    }
}