package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class SwitchIDBusRunLevelOperation extends AbstractInstallOperation implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(SwitchIDBusRunLevelOperation.class);

    private BundleContext bundleContext;

    private int runLevel;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        // Get package admin service.
        ServiceReference ref = getBundleContext().getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
        if (ref == null) {
            throw new LiveUpdateException("StartLevel service is unavailable.");
        }

        try {
            org.osgi.service.startlevel.StartLevel sl = (org.osgi.service.startlevel.StartLevel) getBundleContext().getService(ref);
            if (sl == null) {
                throw new LiveUpdateException("StartLevel service is unavailable.");
            }

            // Switch to maintenance run-level
            sl.setStartLevel(runLevel);

            // TODO : Wait for bundles to shutdown ?!

            return OperationStatus.NEXT;
        }
        finally {
            getBundleContext().ungetService(ref);
        }


    }

    public int getRunLevel() {
        return runLevel;
    }

    public void setRunLevel(int runLevel) {
        this.runLevel = runLevel;
    }
}
