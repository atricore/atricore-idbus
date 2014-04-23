package org.atricore.idbus.idojos.ehcachesessionstore;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.store.session.AbstractSessionStore;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO : We can have a local map of sessions and only laod/save sessions when they are stale.
 * TODO : A save interval property can be defined, just like the EHCache Session store for Jetty. 
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheSessionStore extends AbstractSessionStore implements
        ApplicationContextAware,
        InitializingBean,
        DisposableBean {

    private static final Log logger  = LogFactory.getLog(EHCacheSessionStore .class);

    private boolean init = false;

    private CacheManager cacheManager;

    private Cache cache;

    private String node;

    private String cacheName;
    
    private ApplicationContext applicationContext;

    private List<CacheEventListener> listeners;

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public List<CacheEventListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<CacheEventListener> listeners) {
        this.listeners = listeners;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void init() {

        if (init)
            return;

        synchronized (this) {

            if (init)
                return;

            ClassLoader orig = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

                logger.info("Initializing EHCache Session store using cache " + cacheName);
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

                    for (CacheEventListener listener : listeners) {
                        logger.trace("Cache listener : " + listener);
                        cache.getCacheEventNotificationService().registerListener(listener);
                    }

                    if (logger.isTraceEnabled()) {

                        logger.trace("Initialized EHCache Session store using cache : " + cache);
                        logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                        logger.trace("Cache Bootstrap loader " + cache.getBootstrapCacheLoader());
                        logger.trace("Cache Event Notification service " + cache.getCacheEventNotificationService());
                    }
                }

                logger.info("Initialized EHCache Session store using cache " + cacheName + ". Size: " + cache.getSize());

                init = true;
            } finally {
                Thread.currentThread().setContextClassLoader(orig);
            }
        }
    }

    public void destroy() throws Exception {
        if (init) {
            cacheManager.removeCache(cacheName);
            init = false;
        }
    }

    public int getSize() throws SSOSessionException {
        return cache.getSize();
    }

    public String[] keys() throws SSOSessionException {
        return (String[]) cache.getKeys().toArray(new String[cache.getSize()]);
    }

    public BaseSession[] loadAll() throws SSOSessionException {

        List<BaseSession> allSessions = new ArrayList<BaseSession>();
        
        List keys = cache.getKeys();
        if (keys == null)
            return new BaseSession[0];

        for (Object key : keys) {

            String strKey = (String) key;

            // We have sessions bye username stored in the cache.
            // If a username starts with 'id', the load method can handle it.
            if (strKey.startsWith("id")) {
                BaseSession s = load(strKey);
                if (s != null)
                    allSessions.add(s);
            }
        }

        return allSessions.toArray(new BaseSession[allSessions.size()]);
    }

    public BaseSession load(String id) throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element e = cache.get(id);
            if (e != null) {
                Object value = e.getObjectValue();
                // We have different type of entries,
                if (value instanceof BaseSession) {

                    BaseSession s = (BaseSession) value;
                    // Refresh user sessions access time
                    cache.get(s.getUsername());

                    return s;
                }
            }

            return null;

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public BaseSession[] loadByUsername(String name) throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element e = cache.get(name);
            if (e != null) {

                Object value = e.getObjectValue();

                if (value instanceof List ) {
                    Set<BaseSession> userSessions = (Set<BaseSession>) e.getObjectValue();
                    return userSessions.toArray(new BaseSession[userSessions.size()]);
                }
            }

            return new BaseSession[0];

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public BaseSession[] loadByLastAccessTime(Date time) throws SSOSessionException {
        // TODO : Optimize this !!!
        logger.warn("UNOPTIMIZED Method loadByLastAccessTime");
        return loadAll();
    }

    public BaseSession[] loadByValid(boolean valid) throws SSOSessionException {
        logger.warn("UNOPTIMIZED Method loadByValid");
        List<BaseSession> byValid = new ArrayList<BaseSession>();

        BaseSession[] all = loadAll();
        for (BaseSession baseSession : all) {
            if (baseSession.isValid() == valid)
                byValid.add(baseSession);
        }

        return byValid.toArray(new BaseSession[byValid.size()]);
    }

    public void remove(String id) throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());
            BaseSession s = load(id);
            if (s != null) {
                cache.remove(s.getUsername());
                cache.remove(id);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public void clear() throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());
            cache.removeAll();
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public void save(BaseSession session) throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element s = new Element(session.getId(), session);
            s.setTimeToIdle(session.getMaxInactiveInterval());
            s.setTimeToLive(0);



            // Update user sessions table
            Element u = cache.get(session.getUsername());
            if (u == null) {
                // Concurrent HashMap backing a Set
                Set<BaseSession> sessions = Collections.newSetFromMap(new ConcurrentHashMap<BaseSession, Boolean>());
                u = new Element(session.getUsername(), sessions);
            }

            Set<BaseSession> sessions = (Set<BaseSession>) u.getObjectValue();
            sessions.add(session);
            if (s.getTimeToIdle() > u.getTimeToIdle())
                u.setTimeToIdle(s.getTimeToIdle());

            cache.put(s);
            cache.put(u);

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }
}
