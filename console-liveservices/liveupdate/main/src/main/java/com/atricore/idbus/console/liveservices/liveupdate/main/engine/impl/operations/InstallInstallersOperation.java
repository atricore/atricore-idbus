package com.atricore.idbus.console.liveservices.liveupdate.main.engine.impl.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateContext;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InstallInstallersOperation extends AbstractInstallOperation {

    @Override
    public OperationStatus preInstall(UpdateContext cxt) throws LiveUpdateException {
        // install/start installer osgi bundles
        return OperationStatus.NEXT;
    }
}
