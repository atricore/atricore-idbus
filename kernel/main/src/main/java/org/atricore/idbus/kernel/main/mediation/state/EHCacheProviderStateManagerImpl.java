package org.atricore.idbus.kernel.main.mediation.state;

import net.sf.ehcache.*;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.atricore.idbus.kernel.main.util.UUIDGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheProviderStateManagerImpl implements ProviderStateManager,
        InitializingBean,
        DisposableBean,
        ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(EHCacheProviderStateManagerImpl.class);

    private String cacheName;

    private ConfigurationContext config;

    private CacheManager cacheManager;

    private Cache cache;

    private UUIDGenerator idGen = new UUIDGenerator();

    private boolean forceNonDirtyStorage;

    private boolean init;

    // TODO : Make configurable
    private int receiveRetries = -1;

    private ApplicationContext applicationContext;

    private String namespace;

    private Monitor monitor;

    // Monitor interval (ms)
    private long monitorInterval = 60000;

    private ScheduledThreadPoolExecutor stpe;


    public EHCacheProviderStateManagerImpl() {
    }

    public EHCacheProviderStateManagerImpl(ConfigurationContext config) {
        this.config = config;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public long getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(long monitorInterval) {
        this.monitorInterval = monitorInterval;
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

    public boolean isForceNonDirtyStorage() {
        return forceNonDirtyStorage;
    }

    public void setForceNonDirtyStorage(boolean forceNonDirtyStorage) {
        this.forceNonDirtyStorage = forceNonDirtyStorage;
    }

    public int getReceiveRetries() {
        return receiveRetries;
    }

    public void setReceiveRetries(int receiveRetries) {
        this.receiveRetries = receiveRetries;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void destroy() throws Exception {
        if (init) {
            cacheManager.removeCache(cacheName);
            init = false;
            stpe.shutdown();
        }
    }

    public synchronized void init() {

        if (init)
            return;

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            logger.info("Initializing EHCache Provider State Manager using cache " + cacheName);
            if (cacheManager.cacheExists(cacheName)) {
                logger.info("Cache already exists '"+cacheName+"', reusing it");
                // This is probably a bundle restart, ignore it.
            } else {
                logger.info("Cache does not exists '"+cacheName+"', adding it");
                cacheManager.addCache(cacheName);
            }

            cache = cacheManager.getCache(cacheName);
            cache.getCacheEventNotificationService().registerListener(new CacheStateListener());

            if (cache == null) {
                logger.error("No chache definition found with name '" + cacheName + "'");
                return;
            } else {
                if (logger.isTraceEnabled()) {

                    logger.trace("Initialized EHCache Provider State Manager using cache : " + cache);
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                    logger.trace("Cache Event Notification service " + cache.getCacheEventNotificationService());
                }
            }

            monitor = new Monitor(cache, monitorInterval);
            stpe = new ScheduledThreadPoolExecutor(3);
            // Run the thread every 30 seconds, and start it in 10
            stpe.scheduleAtFixedRate(monitor, 10, monitorInterval, TimeUnit.MILLISECONDS);

            logger.info("Initialized EHCache Provider State Manager using cache " + cacheName + ". Size: " + cache.getSize());

            init = true;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }


    public void store(ProviderStateContext ctx, LocalState s) {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            EHCacheLocalStateImpl state = (EHCacheLocalStateImpl) s;
            if (!forceNonDirtyStorage && !state.isDirty()) {
                if (logger.isTraceEnabled())
                    logger.trace("LocalState instance not dirty for key " + s.getId() + ". Skip storage");
                return;
            }

            synchronized(s) {

                String key = ctx.getProvider().getName() + ":PK:" + state.getId();
                Element element = new Element(key, state);
                cache.put(element);
                if (logger.isTraceEnabled())
                    logger.trace("LocalState instance stored for key " + element.getKey());

                for (String alternativeKeyName : state.getAlternativeIdNames()) {

                    String alternativeKey = ctx.getProvider().getName() + ":" +
                            state.getAlternativeKey(alternativeKeyName);

                    Element alternativeElement = new Element(alternativeKey, key);
                    cache.put(alternativeElement);
                    if (logger.isTraceEnabled())
                        logger.trace("LocalState instance stored for alternative key " + alternativeElement.getKey());
                }

                for (String removedKey : state.getRemovedKeys()) {
                    cache.remove(ctx.getProvider().getName() + ":" +removedKey);
                    if (logger.isTraceEnabled())
                        logger.trace("LocalState instance removed for alternative key " + removedKey);
                }

                state.clearState();
            }
        } finally{
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public LocalState retrieve(ProviderStateContext ctx, String key) {
        return retrieve(ctx, "PK", key);

    }

    public LocalState retrieve(ProviderStateContext ctx, String keyName, String key) {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            String elementKey = ctx.getProvider().getName() + ":" + keyName + ":" + key;
            Element e = retrieveElement(elementKey);

            if (e != null) {
                Object v = e.getObjectValue();

                // Is this a state instance, or an alternative key ?
                if (v instanceof LocalState) {
                    if (logger.isTraceEnabled())
                        logger.trace("LocalState instance found for key [" + elementKey +
                                "] TTL:[" + e.getTimeToLive() + "]" +
                                " TTI:[" + e.getTimeToIdle() + "]" +
                                " LAT:[" + e.getLastAccessTime() + "]");

                    LocalState state = (LocalState) v;
                    refreshState(ctx, (EHCacheLocalStateImpl) state);
                    return (LocalState) v;
                }

                // This is probably an alternate key, the element's value is the primary key
                String pKey = (String) v;
                if (logger.isTraceEnabled())
                    logger.trace("LocalState alternative key found for key [" + elementKey +
                            "/alt:" + pKey + "] TTL:[" + e.getTimeToLive() + "]" +
                            " TTI:[" + e.getTimeToIdle() + "]" +
                            " LAT:[" + e.getLastAccessTime() + "]");

                e = retrieveElement(pKey);
                if (e != null && e.getObjectValue() instanceof LocalState) {
                    if (logger.isTraceEnabled())
                        logger.trace("LocalState instance found for key [" + pKey +
                                "] TTL:[" + e.getTimeToLive() + "]" +
                                " TTI:[" + e.getTimeToIdle() + "]" +
                                " LAT:[" + e.getLastAccessTime() + "]");

                    LocalState state = (LocalState) e.getObjectValue();
                    refreshState(ctx, (EHCacheLocalStateImpl) state);
                    return state;
                }

                // bad ...

            }

            if (logger.isTraceEnabled())
                logger.trace("LocalState instance not found for key " + elementKey);

            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    protected Element retrieveElement(String key) {
        Element e = cache.get(key);
        if (e == null) {

            int retry = 0;
            while (e == null && retry <= receiveRetries) {
                // Wait and try again, maybe state is on the road :)
                if (logger.isTraceEnabled())
                    logger.trace("Cache miss, wait for " + 500 + " ms");

                try { Thread.sleep(500); } catch (InterruptedException ie ) { /* Ignore this */ }
                e = cache.get(key);
                retry ++;
            }
        }

        return e;
    }

    /**
     * This will refresh all the alternate elements
     */
    protected void refreshState(ProviderStateContext ctx, EHCacheLocalStateImpl state) {
        for (String alternativeIdName : state.getAlternativeIdNames()) {
            String alternativeKey = ctx.getProvider().getName() + ":" + state.getAlternativeKey(alternativeIdName);
            // Just to refresh the access time ...
            cache.get(alternativeKey);
            if (logger.isTraceEnabled())
                logger.trace("Accessed LocalState alternative key " + alternativeKey);
        }
    }

    public void remove(ProviderStateContext ctx, String key) {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            String pKey = ctx.getProvider().getName() + ":PK:" + key;
            Element e = cache.get(pKey);
            if (e != null) {
                EHCacheLocalStateImpl state = (EHCacheLocalStateImpl) e.getObjectValue();
                cache.remove(ctx.getProvider().getName() + ":" + key);
                if (logger.isTraceEnabled())
                    logger.trace("Removed LocalState instance for key " + key);

                for (String alternativeIdName : state.getAlternativeIdNames()) {
                    String alternativeKey = ctx.getProvider().getName() + ":" + state.getAlternativeKey(alternativeIdName);
                    cache.remove(alternativeKey);
                    if (logger.isTraceEnabled())
                        logger.trace("Removed LocalState instance for alternative key " + alternativeKey);
                }

            } else {
                if (logger.isTraceEnabled())
                    logger.trace("Cannot remove. LocalState instance not found for key " + key);

            }
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public Collection<LocalState> retrieveAll(ProviderStateContext ctx) {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

        try {
            List keys = cache.getKeys();
            List<LocalState> states = new ArrayList<LocalState>();

            for (Object key1 : keys) {
                String key = (String) key1;
                if (!key.startsWith(ctx.getProvider().getName() + ":PK:"))
                    continue;

                Element element = cache.get(key);
                if (element.getObjectValue() instanceof LocalState) {
                    EHCacheLocalStateImpl s = (EHCacheLocalStateImpl) element.getObjectValue();
                    s.setNew(false);
                    states.add(s);
                }
            }

            return states;

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }

    }

    public LocalState createState(ProviderStateContext ctx) {
        EHCacheLocalStateImpl state = new EHCacheLocalStateImpl(idGen.generateId());
        state.setNew(true);
        store(ctx, state);

        if (logger.isTraceEnabled())
            logger.trace("Created new LocalState instance with Key " + state.getId());

        return state;
    }


    class CacheStateListener implements CacheEventListener {

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

            if (element.getObjectValue() instanceof LocalState) {

                LocalState state = (LocalState) element.getObjectValue();

                if (logger.isDebugEnabled())
                    logger.debug("Removed provider state" + state.getId());

                cache.remove(element.getObjectKey());

                for (String altKey : state.getAlternativeIdNames()) {

                    if (logger.isDebugEnabled())
                        logger.debug("Removed provider alt-key (from state) " + altKey);

                    cache.remove(altKey);
                }

            } else {

                if (logger.isDebugEnabled())
                    logger.debug("Removed provider alt-key " + element.getObjectKey());

                cache.remove(element.getObjectKey());
            }


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
                cache.evictExpiredElements();
                cache.getKeysWithExpiryCheck();
                long execTime = now - System.currentTimeMillis();

                if (execTime > 1000)
                    logger.warn("Provider state manager cache [" + cache.getName() + "] needs tuning. getKeysWithExpiryCheck(): exec=" + execTime + "ms");

                if (logger.isTraceEnabled())
                    logger.trace("Evicted (aprox) " + (size - cache.getSize()) + " elements from " + cache.getName());

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
