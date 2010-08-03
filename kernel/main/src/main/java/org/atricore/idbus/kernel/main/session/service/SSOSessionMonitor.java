package org.atricore.idbus.kernel.main.session.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */

/**
 * Checks for valid sessions every second.
 */
class SessionMonitor implements Runnable {

    private static final Log logger = LogFactory.getLog(SessionMonitor.class);

    private long _interval;

    private SSOSessionManager _m;

    private boolean _started = true;

    SessionMonitor(SSOSessionManager m) {
        _m = m;
    }

    SessionMonitor(SSOSessionManager m, long interval) {
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
