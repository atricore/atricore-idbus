package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * // TODO : Defien operaiton bean
 * // TODO : Configure operation in updates-system-start
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class WaitForSystemStartup extends AbstractInstallOperation implements BundleContextAware {

    private BundleContext bundleContext;

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {

        // TODO : check if all bundles are started for our current run-level.
        // Get reference to org.osgi.service.startlevel.StartLevel (see witchIDBusRunLevelOperation)
        // check that all bundles in that runlevel are active
        // if (true, return next, otherwise, return pause)

        // org.osgi.service.startlevel.StartLevel sl;
        // sl.getBundleStartLevel(bundle);

        return super.execute(event);
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
