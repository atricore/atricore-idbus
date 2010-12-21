package com.atricore.idbus.console.liveservices.liveupdate.main.service;

import com.atricore.idbus.console.liveservices.liveupdate.main.LiveUpdateManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UpdatesMonitor implements Runnable {

    private static final Log logger = LogFactory.getLog(UpdatesMonitor.class);

    private long _interval;

    private LiveUpdateManager _m;

    private boolean _started = true;

    UpdatesMonitor(LiveUpdateManager m) {
        _m = m;
    }

    UpdatesMonitor(LiveUpdateManager m, long interval) {
        _interval = interval;
        _m = m;
    }

    public long getInterval() {
        return _interval;
    }

    public void setInterval(long interval) {
        _interval = interval;
    }

    /**
     * Check for valid sessions ...
     */
    public void run() {

        try {

            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkForUpdates ... ");

            _m.checkForUpdates();

            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkForUpdates ... DONE");


        } catch (Exception e) {
            logger.error("[run()] calling checkForUpdates ... ERROR:" + e.getMessage());

            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkForUpdates ... ERROR:" + e.getMessage(), e);
        }


    }

}
