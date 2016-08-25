package org.atricore.idbus.kernel.main.provisioning.impl;

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
 * Created by sgonzalez.
 */
public class EHCacheTransactionStore implements TransactionStore , InitializingBean,
        DisposableBean,
        ApplicationContextAware {

    private static Log logger = LogFactory.getLog(EHCacheTransactionStore.class);

    private String cacheName;

    private CacheManager cacheManager;

    private Cache cache;

    private boolean init = false;

    private ApplicationContext applicationContext;

    private int expiredTransactionsTimeToLiveSecs = 3600;

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

            logger.info("Initializing EHCache Transaction Store using cache " + cacheName);
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

                    logger.trace("Initialized EHCache Transaction Store using cache : " + cache);
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Event Notification service " + cache.getCacheEventNotificationService());
                }
            }

            logger.info("Initialized EHCache Transaction Store using cache " + cacheName + ". Size: " + cache.getSize());

            init = true;
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

    public int getExpiredTransactionsTimeToLiveSecs() {
        return expiredTransactionsTimeToLiveSecs;
    }

    public void setExpiredTransactionsTimeToLiveSecs(int expiredTransactionsTimeToLiveSecs) {
        this.expiredTransactionsTimeToLiveSecs = expiredTransactionsTimeToLiveSecs;
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
    public PendingTransaction remove(String idOrCode) {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());
            Element e = cache.get(idOrCode);

            if (e != null) {
                PendingTransaction t = (PendingTransaction) e.getObjectValue();
                cache.remove(t.getId());
                if (t.getCode() != null)
                    cache.remove(t.getCode());

                return t;
            }
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }

        return null;

    }

    @Override
    public PendingTransaction retrieve(String idOrCode) {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

                Element e = cache.get(idOrCode);
            if (logger.isTraceEnabled())
                logger.trace("Transaction ID or Code " + idOrCode + (e != null ? "" : " not") + " found in cache " + cacheName);

            if (e != null) {
                PendingTransaction t = (PendingTransaction) e.getObjectValue();
                return t;
            }

            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    @Override
    public void store(PendingTransaction transaction) {

        long now = System.currentTimeMillis();
        int ttlInSecs = (int) ((transaction.getExpiresOn() - now) / 1000L) + expiredTransactionsTimeToLiveSecs;

        Element transactionById = new Element(transaction.getId(), transaction);
        transactionById.setTimeToLive(ttlInSecs);
        transactionById.setTimeToIdle(ttlInSecs);
        transactionById.setEternal(false);

        if (logger.isTraceEnabled())
            logger.trace("Stored transaction by ID " + transaction.getId() + " in cache " + cacheName + " ttl (secs) : " + ttlInSecs);

        cache.put(transactionById);

        if (transaction.getCode() != null) {
            Element transactionByCode = new Element(transaction.getCode(), transaction);
            transactionByCode.setTimeToLive(ttlInSecs);
            transactionByCode.setTimeToIdle(ttlInSecs);
            transactionByCode.setEternal(false);

            cache.put(transactionByCode);

            if (logger.isTraceEnabled())
                logger.trace("Stored transaction by Code " + transaction.getId() + " in cache " + cacheName + " ttl (secs) : " + ttlInSecs);
        }

    }
}
