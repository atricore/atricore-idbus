package org.atricore.idbus.kernel.main.mediation.state;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
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

    private int receiveRetries = 3;

    private ApplicationContext applicationContext;

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

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void destroy() throws Exception {
        if (init) {
            cacheManager.removeCache(cacheName);
            init = false;
        }
    }

    public synchronized void init() {

        if (init)
            return;

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            logger.info("Initializing EHCache Provider State Manager using cache " + cacheName);
            if (cacheManager.cacheExists(cacheName))
                throw new IllegalStateException("Cache already exists '"+cacheName+"'");

            cacheManager.addCache(cacheName);
            cache = cacheManager.getCache(cacheName);

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

                Element element = new Element(ctx.getProvider().getName() + ":PK:" + state.getId(), state);
                // Set provider state specific timeouts (in seconds)
                //element.setTimeToIdle(1800);
                //element.setTimeToLive(43200);
                cache.put(element);
                if (logger.isTraceEnabled())
                    logger.trace("LocalState instance stored for key " + element.getKey());

                for (String alternativeKeyName : state.getAlternativeIdNames()) {

                    String alternativeKey = ctx.getProvider().getName() + ":" +
                            state.getAlternativeKey(alternativeKeyName);

                    Element alternativeElement = new Element(alternativeKey, state);
                    cache.put(alternativeElement);
                    if (logger.isTraceEnabled())
                        logger.trace("LocalState instance stored for alternative key " + alternativeElement.getKey());
                }

                for (String removedKey : state.getRemovedKeys()) {
                    cache.remove(ctx.getProvider().getName() + ":" +removedKey);
                    if (logger.isTraceEnabled())
                        logger.trace("LocalState instance removed for alternative key " + removedKey);
                }

                // Give time to flush messages TODO : Improve this !!!!
                // WTF !? try { Thread.sleep(1000); } catch (InterruptedException ie) { /**/ }

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

            Element e = cache.get(ctx.getProvider().getName() + ":" + keyName + ":" + key);
            if (e == null) {

                int retry = 0;
                while (e == null && retry <= receiveRetries) {
                    // Wait and try again, maybe state is on the road :)
                    if (logger.isTraceEnabled())
                        logger.trace("Cache miss, wait for " + 500 + " ms");

                    try { Thread.sleep(500); } catch (InterruptedException ie ) { /* Ignore this */ }
                    e = cache.get(ctx.getProvider().getName() + ":" + keyName + ":" + key);
                    retry ++;
                }
            }

            if (e != null) {
                if (logger.isTraceEnabled())
                    logger.trace("LocalState instance found for key " + key);

                return (LocalState) e.getValue();
            }

            if (logger.isTraceEnabled())
                logger.trace("No LocalState instance not found for key " + key);

            return null;
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public void remove(ProviderStateContext ctx, String key) {

        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element e = cache.get(ctx.getProvider().getName() + ":PK:" + key);
            if (e != null) {
                EHCacheLocalStateImpl state = (EHCacheLocalStateImpl) e.getValue();
                cache.remove(ctx.getProvider().getName() + ":" + key);
                if (logger.isTraceEnabled())
                    logger.trace("Removed LocalState instance for key " + key);

                for (String alternativeIdName : state.getAlternativeIdNames()) {
                    String alternativeKey = state.getAlternativeKey(alternativeIdName);
                    cache.remove(ctx.getProvider().getName() + ":" + alternativeKey);
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
                if (element.getValue() instanceof LocalState) {
                    states.add((LocalState) element.getValue());
                }
            }

            return states;

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }

    }

    public LocalState createState(ProviderStateContext ctx) {
        EHCacheLocalStateImpl state = new EHCacheLocalStateImpl(idGen.generateId());
        store(ctx, state);

        if (logger.isTraceEnabled())
            logger.trace("Created new LocalState instance with Key " + state.getId());

        return state;
    }


}
