package org.atricore.idbus.kernel.main.session.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */

/**
 * Checks for valid sessions every second.
 */
public class DefaultSessionMonitor implements SSOSessionMonitor, Runnable {

    private static final Log logger = LogFactory.getLog(DefaultSessionMonitor.class);

    private long _interval;

    private SSOSessionManagerImpl _m;

    private boolean _started = true;

    private ScheduledThreadPoolExecutor stpe;



    DefaultSessionMonitor(SSOSessionManagerImpl m) {
        _m = m;
        _interval = m.getSessionMonitorInterval();
    }

    DefaultSessionMonitor(SSOSessionManagerImpl m, long interval) {
        _interval = interval;
        _m = m;
    }

    public void start() {
        stpe = new ScheduledThreadPoolExecutor(3);
        stpe.scheduleAtFixedRate(this, _interval, _interval, TimeUnit.MILLISECONDS);
        _started = true;
    }

    public void stop() {
        stpe.shutdown();
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
                logger.trace("[run()] calling checkValidSessions ... ");

            _m.checkValidSessions();

            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkValidSessions ... DONE");


        } catch (Exception e) {
            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkValidSessions ... ERROR:" + e.getMessage(), e);
        }


    }

}
