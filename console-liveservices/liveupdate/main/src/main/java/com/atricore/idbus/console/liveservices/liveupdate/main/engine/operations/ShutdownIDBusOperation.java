package com.atricore.idbus.console.liveservices.liveupdate.main.engine.operations;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.InstallEvent;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.OperationStatus;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.AbstractInstallOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ShutdownIDBusOperation extends AbstractInstallOperation implements BundleContextAware {

    private static final Log logger = LogFactory.getLog(ShutdownIDBusOperation.class);

    private BundleContext bundleContext;

    @Override
    public OperationStatus execute(InstallEvent event) throws LiveUpdateException {
        new Thread() {
            public void run() {
                try {
                    Bundle bundle = bundleContext.getBundle(0);
                    bundle.stop();
                } catch (Exception e) {
                    logger.error("Error when shutting down Atricore IDBus : " + e.getMessage(), e);
                }
            }
        }.start();
        return OperationStatus.PAUSE;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }
}