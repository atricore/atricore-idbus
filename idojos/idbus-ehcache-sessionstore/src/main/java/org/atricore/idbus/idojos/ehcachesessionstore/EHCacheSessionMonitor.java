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

    private long monitorInterval = 10000;

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
        stpe.scheduleAtFixedRate(monitor, 10, 30, TimeUnit.SECONDS);
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

    }

    public void notifyElementPut(Ehcache ehcache, Element e) throws CacheException {
    }

    public void notifyElementUpdated(Ehcache ehcache, Element e) throws CacheException {

    }

    public void notifyElementExpired(Ehcache ehcache, Element e) {

        if (!(e.getObjectValue() instanceof BaseSession))
            return;

        BaseSession session = (BaseSession) e.getObjectValue();
        if (logger.isDebugEnabled())
            logger.debug("Cache element expired ["+ehcache.getName()+"], session ["+session.getUsername()+"] " + session.getId());

        manager.checkValidSessions(new BaseSession[]{session});

    }

    public void notifyElementEvicted(Ehcache ehcache, Element e) {

    }

    public void notifyRemoveAll(Ehcache ehcache) {

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
                cache.evictExpiredElements();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
