package org.atricore.idbus.kernel.main.util;

import net.sf.ehcache.*;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EHCacheIdRegistry implements IdRegistry, InitializingBean,
        DisposableBean,
        ApplicationContextAware {

    private static Log logger = LogFactory.getLog(EHCacheIdRegistry.class);

    private String cacheName;

    private CacheManager cacheManager;

    private Cache cache;

    private long cachePurgeLastRun = 0;

    // check cache elements for expiration every 3 minutes
    private long cachePurgeInterval = 3 * 60 * 1000;

    private boolean init = false;

    // 5 minute for cache entries
    private int defaultTimeToLive = 5 * 60;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public synchronized void init() {

        if (init)
            return;

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            logger.info("Initializing EHCache ID Registry using cache " + cacheName);
            if (cacheManager.cacheExists(cacheName)) {
                logger.info("Cache already exists '"+cacheName+"', reusing it");
                // This is probably a bundle restart, ignore it.
            } else {
                logger.info("Cache does not exists '"+cacheName+"', adding it");
                cacheManager.addCache(cacheName);
            }

            cache = cacheManager.getCache(cacheName);
            cache.getCacheEventNotificationService().registerListener(new IDRegistryCacheStateListener());

            if (cache == null) {
                logger.error("No cache definition found with name '" + cacheName + "'");
                return;
            } else {
                if (logger.isTraceEnabled()) {

                    logger.trace("Initialized EHCache ID Registry using cache : " + cache);
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Event Notification service " + cache.getCacheEventNotificationService());
                }
            }

            logger.info("Initialized EHCache ID Registry using cache " + cacheName + ". Size: " + cache.getSize());

            init = true;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (init) {
            cacheManager.removeCache(cacheName);
            init = false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public boolean isUsed(String id) {
        Element e = cache.get(id);
        if (logger.isTraceEnabled())
            logger.trace("ID " + id + (e != null ? "" : " not") + " found in cache " + cacheName);

        return e != null;
    }

    @Override
    public void register(String id) {
        register(id, defaultTimeToLive);
    }

    @Override
    public void register(String id, int timeToLive) {
        purgeCache();

        Element e = new Element(id, id);
        e.setTimeToLive(timeToLive);
        e.setTimeToIdle(timeToLive);
        e.setEternal(false);

        cache.put(e);

        if (logger.isTraceEnabled())
            logger.trace("Stored ID " + id + " in cache " + cacheName);

    }

    protected void purgeCache() {
        long now = System.currentTimeMillis();

        logger.trace("Running: " + cachePurgeLastRun + ", " + cachePurgeInterval);

        // Still not needed to run ...
        if (cachePurgeLastRun  + cachePurgeInterval > now)
            return;

        try {
            if (logger.isTraceEnabled())
                logger.trace("Checking expired elements for " + cache.getName());

            cachePurgeLastRun = now;
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
            cache.evictExpiredElements();
            cache.getKeysWithExpiryCheck();

            long execTime = now - System.currentTimeMillis();

            if (execTime > 1000)
                logger.warn("ID registry cache [" + cache.getName() + "] needs tuning. getKeysWithExpiryCheck(): exec=" + execTime + "ms");

            if (logger.isTraceEnabled())
                logger.trace("Evicted (aprox) " + (size - cache.getSize()) + " elements from " + cache.getName() + ". Current cache size is " + cache.getSize());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    class IDRegistryCacheStateListener implements CacheEventListener {

        @Override
        public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
            if (logger.isTraceEnabled())
                logger.trace("notifyElementRemoved:" + ehcache.getName() + "/" +  element.getObjectKey());
        }

        @Override
        public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
            if (logger.isTraceEnabled())
                logger.trace("notifyElementPut:" + ehcache.getName() + "/" + element.getObjectKey());
        }

        @Override
        public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
            if (logger.isTraceEnabled())
                logger.trace("notifyElementUpdated:" + ehcache.getName() + "/" + element.getObjectKey());
        }

        @Override
        public void notifyElementExpired(Ehcache ehcache, Element element) {
            if (logger.isTraceEnabled())
                logger.trace("notifyElementExpired:" + ehcache.getName() + "/" + element.getObjectKey());

            cache.remove(element.getObjectKey());
        }

        @Override
        public void notifyElementEvicted(Ehcache ehcache, Element element) {
            if (logger.isTraceEnabled())
                logger.trace("notifyElementEvicted:" + ehcache.getName() + "/" + element.getObjectKey());
        }

        @Override
        public void notifyRemoveAll(Ehcache ehcache) {
            logger.trace("notifyRemoveAll:" + ehcache.getName() );
        }

        @Override
        public void dispose() {

        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

}
