package org.atricore.idbus.capabilities.sts.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
class TokenMonitor implements Runnable {

    private static final Log logger = LogFactory.getLog(TokenMonitor.class);

    private long _interval;

    private WSTSecurityTokenService _m;

    private boolean _started = true;

    TokenMonitor(WSTSecurityTokenService m) {
        _m = m;
    }

    TokenMonitor(WSTSecurityTokenService m, long interval) {
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
                logger.trace("[run()] calling checkExpiredTokens ... ");

            _m.checkExpiredTokens();

            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkExpiredTokens ... DONE");


        } catch (Exception e) {
            if (logger.isTraceEnabled())
                logger.trace("[run()] calling checkExpiredTokens ... ERROR:" + e.getMessage(), e);
        }


    }

}
