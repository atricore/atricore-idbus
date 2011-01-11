package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateException;
import com.atricore.idbus.console.liveservices.liveupdate.main.engine.UpdateEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class SystemStartupMonitor implements Runnable {

    private static final Log logger = LogFactory.getLog(SystemStartupMonitor.class);

    private BundleContext bundleContext;
    
    private UpdateEngine engine;

    private int runLevel;

    private boolean stopProcessing;
    
    public SystemStartupMonitor(UpdateEngine engine, int runLevel, BundleContext bundleContext) {
        this.engine = engine;
        this.runLevel = runLevel;
        this.bundleContext = bundleContext;
    }
    
    public void run() {
        if (!stopProcessing) {
            ServiceReference ref = bundleContext.getServiceReference(org.osgi.service.startlevel.StartLevel.class.getName());
            if (ref == null) {
                logger.error("StartLevel service is unavailable.");
                return;
            }

            try {
                org.osgi.service.startlevel.StartLevel sl = (org.osgi.service.startlevel.StartLevel) bundleContext.getService(ref);
                if (sl == null) {
                    logger.error("StartLevel service is unavailable.");
                    return;
                }

                if (sl.getStartLevel() == runLevel) {
                    engine.resumeAll();
                } else {
                    stopProcessing = true;
                }
            } catch (LiveUpdateException e) {
                logger.error("Error resuming update processes: " + e.getMessage(), e);
            } finally {
                bundleContext.ungetService(ref);
            }
        }
	}
}
