package org.atricore.idbus.idojos.ehcachesessionstore;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.service.SSOSessionMonitor;
import org.springframework.beans.factory.InitializingBean;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EHCacheSessionMonitor implements SSOSessionMonitor, CacheEventListener, InitializingBean {

    private static final Log logger = LogFactory.getLog(EHCacheSessionMonitor.class);


    private String cacheName;

    private SSOSessionManager manager;

    private Monitor monitor;

    private ScheduledThreadPoolExecutor stpe;

    // Monitor interval (ms), Run once per-minute by default
    private long monitorInterval = 60000;

    public EHCacheSessionMonitor() {

    }

    public EHCacheSessionMonitor(SSOSessionManager manager) {
        this.manager = manager;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void start() {
        EHCacheSessionStore store = (EHCacheSessionStore) manager.getSessionStore();
        monitor = new Monitor(store.getCache(), monitorInterval);
        stpe = new ScheduledThreadPoolExecutor(3);
        // Run the thread every 30 seconds, and start it in 10
        stpe.scheduleAtFixedRate(monitor, 10, monitorInterval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (stpe != null)
            stpe.shutdown();

        stpe = null;
        monitor = null;
    }

    public void setInterval(long millis) {
        this.monitorInterval = millis;
    }

    public void notifyElementRemoved(Ehcache ehcache, Element e) throws CacheException {
        if (logger.isTraceEnabled())
            logger.trace("Removed " + e.getObjectKey());
    }

    public void notifyElementPut(Ehcache ehcache, Element e) throws CacheException {
        if (logger.isTraceEnabled())
            logger.trace("Put " + e.getObjectKey());
    }

    public void notifyElementUpdated(Ehcache ehcache, Element e) throws CacheException {
        if (logger.isTraceEnabled())
            logger.trace("Updated " + e.getObjectKey());
    }

    public void notifyElementExpired(Ehcache ehcache, Element e) {

        if (logger.isTraceEnabled())
            logger.trace("Expired " + e.getObjectKey());


        if (!(e.getObjectValue() instanceof BaseSession))
            return;

        BaseSession session = (BaseSession) e.getObjectValue();
        if (logger.isDebugEnabled())
            logger.debug("Cache element expired ["+ehcache.getName()+"], session ["+session.getUsername()+"] " + session.getId());

        manager.checkValidSessions(new BaseSession[]{session});

    }

    public void notifyElementEvicted(Ehcache ehcache, Element e) {
        if (logger.isTraceEnabled())
            logger.trace("Evicted " + e.getObjectKey());
    }

    public void notifyRemoveAll(Ehcache ehcache) {
        if (logger.isTraceEnabled())
            logger.trace("removeAll ");

    }

    public void dispose() {

    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public SSOSessionManager getManager() {
        return manager;
    }

    public void setManager(SSOSessionManager manager) {
        this.manager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // to shut up check-style, why do we need this ?
        super.clone();
        throw new CloneNotSupportedException();
    }

    public class Monitor implements Runnable {

        private Cache cache;

        private long lastRun = 0;

        private long sessionMonitorInterval;

        public Monitor(Cache cache, long sessionMonitorInterval) {
            this.cache = cache;
            this.sessionMonitorInterval = sessionMonitorInterval;
        }

        public Cache getCache() {
            return cache;
        }

        public void setCache(Cache cache) {
            this.cache = cache;
        }

        public long getSessionMonitorInterval() {
            return sessionMonitorInterval;
        }

        public void setSessionMonitorInterval(long sessionMonitorInterval) {
            this.sessionMonitorInterval = sessionMonitorInterval;
        }

        @Override
        public void run() {
            long now = System.currentTimeMillis();

            // Still not needed to run ...
            if (lastRun  + sessionMonitorInterval > now)
                return;

            try {

                if (logger.isTraceEnabled())
                    logger.trace("Checking expired elements for " + cache.getName());

                lastRun = now;
                long size = cache.getSize();

                // -------------------------------------------------
                // The time taken is O(n).
                // On a single cpu 1.8Ghz P4, approximately 8ms is required for each 1000 entries.
                // -------------------------------------------------
                // List allKeys = cache.getKeys();

                // -------------------------------------------------
                // Very expensive call when caches are large ...
                // This will trigger the expired event !
                // -------------------------------------------------
                cache.getKeysWithExpiryCheck();
                long execTime = now - System.currentTimeMillis();

                if (execTime > 1000)
                    logger.warn("SSO Session cache [" + cache.getName() + "] needs tuning. getKeysWithExpiryCheck(): exec=" + execTime + "ms");

                if (logger.isTraceEnabled())
                    logger.trace("Evicted (aprox) " + (size - cache.getSize()) + " elements from " + cache.getName());

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
