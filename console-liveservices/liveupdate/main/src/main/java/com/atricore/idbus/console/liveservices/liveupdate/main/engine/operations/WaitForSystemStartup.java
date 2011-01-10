package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class WaitForSystemStartup extends AbstractInstallOperation implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(WaitForSystemStartup.class);

    private BundleContext bundleContext;

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        ServiceReference ref = getBundleContext().getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
        if (ref == null) {
            throw new LiveUpdateException("StartLevel service is unavailable.");
        }

        try {
            org.osgi.service.startlevel.StartLevel sl = (org.osgi.service.startlevel.StartLevel) getBundleContext().getService(ref);
            if (sl == null) {
                throw new LiveUpdateException("StartLevel service is unavailable.");
            }

            int startLevel = sl.getStartLevel();
            logger.info("Current RUN-LEVEL: " + startLevel);

            for (Bundle bundle : getBundleContext().getBundles()) {
                if (sl.getBundleStartLevel(bundle) <= startLevel && bundle.getState() != Bundle.ACTIVE) {
                    return OperationStatus.PAUSE;
                }
            }

            return OperationStatus.NEXT;
        }
        finally {
            getBundleContext().ungetService(ref);
        }
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
