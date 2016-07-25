package org.atricore.idbus.capabilities.sts.main;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import net.sf.ehcache.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by sgonzalez.
 */
public class EHCacheSecurityTokenStore implements TokenStore, ApplicationContextAware, DisposableBean, InitializingBean {

    private static final Log logger = LogFactory.getLog(EHCacheSecurityTokenStore.class);

    private CacheManager cacheManager;

    private Cache cache;

    private String cacheName;

    private boolean init = false;

    // 1 hour (3600 secs)
    private int defaultTimteToLive = 60 * 60;

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public synchronized void init() {

        if (init)
            return;

        // Use the thread classloader (appliance classloader) instead of STS bundle classloader
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            logger.info("Initializing EHCache Token Store using cache " + cacheName);
            if (cacheManager.cacheExists(cacheName)) {
                logger.info("Cache already exists '"+cacheName+"', reusing it");
                // This is probably a bundle restart, ignore it.
            } else {
                logger.info("Cache does not exists '"+cacheName+"', adding it");
                cacheManager.addCache(cacheName);
            }

            cache = cacheManager.getCache(cacheName);

            if (cache == null) {
                logger.error("No cache definition found with name '" + cacheName + "'");
                return;
            } else {
                if (logger.isTraceEnabled()) {

                    logger.trace("Initialized EHCache Token Store using cache : " + cache);
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Event Notification service " + cache.getCacheEventNotificationService());
                }
            }

            logger.info("Initialized EHCache Token Store using cache " + cacheName + ". Size: " + cache.getSize());

            init = true;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Override
    public void shutdown() {
        if (init) {
            cacheManager.removeCache(cacheName);
            init = false;
        }
    }

    @Override
    public SecurityToken retrieve(String tokenId) {

        // Use the thread classloader (appliance classloader) instead of STS bundle classloader
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element e = cache.get(tokenId);
            SecurityToken st = (SecurityToken) e.getObjectValue();

            if (st.getExpiresOn() < System.currentTimeMillis()) {
                logger.debug("Token found, but has expired: " + st.getId());
                return null;
            }

            return st;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Override
    public void store(SecurityToken token) {
        // Use the thread classloader (appliance classloader) instead of STS bundle classloader
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());
            int timeToLive = (int) (token.getExpiresOn() - System.currentTimeMillis());

            Element e = new Element(token.getId(), token);
            e.setTimeToLive(timeToLive);
            e.setTimeToIdle(timeToLive);
            e.setEternal(false);

            cache.put(e);
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public int getDefaultTimteToLive() {
        return defaultTimteToLive;
    }

    public void setDefaultTimteToLive(int defaultTimteToLive) {
        this.defaultTimteToLive = defaultTimteToLive;
    }
}
