package org.atricore.idbus.kernel.main.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
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

    private boolean init = false;

    // 1 hour (3600 secs)
    private int defaultTimteToLive = 60 * 60;

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

            if (cache == null) {
                logger.error("No chache definition found with name '" + cacheName + "'");
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
        register(id, defaultTimteToLive);
    }

    @Override
    public void register(String id, int timeToLive) {

        Element e = new Element(id, id);
        e.setTimeToLive(timeToLive);
        e.setTimeToIdle(timeToLive);
        e.setEternal(false);

        cache.put(e);

        if (logger.isTraceEnabled())
            logger.trace("Stored ID " + id + " in cache " + cacheName);
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
}
