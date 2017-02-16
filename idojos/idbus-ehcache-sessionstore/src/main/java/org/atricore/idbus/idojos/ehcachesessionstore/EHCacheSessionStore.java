package org.atricore.idbus.idojos.ehcachesessionstore;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.distribution.RMICacheManagerPeerProvider;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.store.session.AbstractSessionStore;
import org.atricore.idbus.bundles.ehcache.distribution.DynamicRMICacheManagerPeerProvider;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
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

    private int loadRetryCount = -1;

    private long loadRetryDelay = 100;

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

    public int getLoadRetryCount() {
        return loadRetryCount;
    }

    public void setLoadRetryCount(int loadRetryCount) {
        this.loadRetryCount = loadRetryCount;
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
                    registerPeers();
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

                Map<String, ConfigurationContext> ctxs = applicationContext.getBeansOfType(ConfigurationContext.class);

                if (ctxs.size() == 1) {
                    ConfigurationContext ctx = ctxs.values().iterator().next();

                    String loadRetryCountStr = ctx.getProperty("sessionstore.loadRetryCount");
                    String loadRetryDelayStr = ctx.getProperty("sessionstore.loadRetryDelay");

                    if (loadRetryCountStr != null) {
                        loadRetryCount = Integer.parseInt(loadRetryCountStr);
                        logger.trace("Load retry count       " + loadRetryCount);
                    }

                    if (loadRetryDelayStr != null) {
                        loadRetryDelay = Long.parseLong(loadRetryDelayStr);
                        logger.trace("Load retry delay       " + loadRetryDelay);
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
            unregisterPeers();
            cacheManager.removeCache(cacheName);
            init = false;
        }
    }

    public int getSize() throws SSOSessionException {
        int size = cache.getSize();
        int mod = size / 2;

        if (mod != 0)
            size ++;

        return size / 2;
    }

    public String[] keys() throws SSOSessionException {
        return (String[]) cache.getKeys().toArray(new String[cache.getSize()]);
    }

    public BaseSession[] loadAll() throws SSOSessionException {

        List<BaseSession> allSessions = new ArrayList<BaseSession>();
        // Only load keys for non-expired elements ?!
        List keys = cache.getKeysWithExpiryCheck();
        if (keys == null)
            return new BaseSession[0];

        for (Object key : keys) {

            String strKey = (String) key;

            // We have sessions bye username stored in the cache.
            // If a username starts with 'id', the load method can handle it.
            if (strKey.startsWith("id")) {
                BaseSession s = load(strKey, true);
                if (s != null)
                    allSessions.add(s);
            }
        }

        return allSessions.toArray(new BaseSession[allSessions.size()]);
    }

    public BaseSession load(String id) throws SSOSessionException {
        return load(id, false);
    }

    public BaseSession[] loadByUsername(String name) throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element e = retrieveElement(name, false);

            if (e != null) {

                Object value = e.getObjectValue();

                if (value instanceof Set ) {

                    Set<String> sessionKeys = (Set<String>) e.getObjectValue();
                    List<BaseSession> userSessions = new ArrayList<BaseSession>();

                    if (sessionKeys != null) {
                        for (String key : sessionKeys) {
                            BaseSession session = load(key);
                            if (session != null)
                                userSessions.add(session);
                        }

                        if (logger.isTraceEnabled())
                            logger.trace("Loaded sessions " + userSessions.size() + " for user " + name);

                        return userSessions.toArray(new BaseSession[userSessions.size()]);
                    }
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
            BaseSession s = load(id, true);
            if (s != null) {

                // Remove this session
                cache.remove(id);

                // Update user sessions list
                Element e = retrieveElement(s.getUsername(), true);
                Set<String> sessionKeys = (Set<String>) e.getObjectValue();
                sessionKeys.remove(id);
                cache.put(e);

                if (logger.isTraceEnabled())
                    logger.trace("Removed session " + s.getId() + " for user " + s.getUsername());

            }
        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    public void clear() throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            if (logger.isTraceEnabled())
                logger.trace("Removing all session (clear)");

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
            // Make sure that the cache element expires after the session, so that when this condition is triggered the session
            // will be stale and therefore ready to be disposed.
            s.setTimeToIdle(session.getMaxInactiveInterval() + 60);
            // Let's put a limit - 12 hs - to the life of the cache element so that in case it's not explicitly removed, the
            // the cache manager will.
            s.setTimeToLive(12 * 60 * 60);

            // Update user sessions table
            // Concurrency should be low, a user normally does not have that many sessions
            Element u = retrieveElement(session.getUsername(), false);
            if (u == null) {
                // Concurrent HashMap backing a Set
                Set<String> sessions = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
                u = new Element(session.getUsername(), sessions);
                u.setTimeToIdle(s.getTimeToIdle());
                u.setTimeToLive(s.getTimeToLive());
            }

            Set<String> sessions = (Set<String>) u.getObjectValue();
            sessions.add(session.getId());

            if (s.getTimeToIdle() > u.getTimeToIdle())
                u.setTimeToIdle(s.getTimeToIdle());

            cache.put(s);
            cache.put(u);

            if (logger.isTraceEnabled())
                logger.trace("Saved session " + session.getId() + " for user " + session.getUsername());

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }

    protected BaseSession load(String id, boolean quiet) throws SSOSessionException {
        ClassLoader orig = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(applicationContext.getClassLoader());

            Element e = retrieveElement(id, quiet);
            if (e != null) {
                Object value = e.getObjectValue();
                // We have different type of entries,
                if (value instanceof BaseSession) {

                    BaseSession s = (BaseSession) value;

                    // Refresh user sessions access time
                    retrieveElement(s.getUsername(), quiet);

                    if (logger.isTraceEnabled())
                        logger.trace("Loaded session " + s.getId() + " for user " + s.getUsername());

                    return s;
                }
            }

            return null;

        } finally {
            Thread.currentThread().setContextClassLoader(orig);
        }
    }


    protected Element retrieveElement(String key, boolean quiet) {
        Element e;
        e = quiet ? cache.getQuiet(key) : cache.get(key);

        if (e == null) {

            int retry = 0;
            while (e == null && retry < loadRetryCount) {
                // Wait and try again, maybe state is on the road :)
                if (logger.isTraceEnabled())
                    logger.trace("Cache miss, wait for " + loadRetryDelay + " ms");

                try { Thread.sleep(loadRetryDelay); } catch (InterruptedException ie ) { /* Ignore this */ }
                e = quiet ? cache.getQuiet(key) : cache.get(key);
                retry ++;
            }
        }

        return e;
    }

    protected void registerPeers() {

        RMICacheManagerPeerProvider peerProvider =
                (RMICacheManagerPeerProvider) cacheManager.getCacheManagerPeerProvider("RMI");

        // Not a clustered environment
        if (peerProvider == null)
            return;

        if (peerProvider instanceof DynamicRMICacheManagerPeerProvider) {
            DynamicRMICacheManagerPeerProvider dynamicPeerProvider = (DynamicRMICacheManagerPeerProvider) peerProvider;
            Collection<String> remoteHosts = dynamicPeerProvider.listRemoteHosts();
            for (String remoteHost : remoteHosts) {
                String rmiUrl = "//" + remoteHost + "/" + cacheName;
                logger.info("Registering remote cache : " + rmiUrl);
                dynamicPeerProvider.registerPeer(rmiUrl);
            }
        }
    }

    protected void unregisterPeers() {

        RMICacheManagerPeerProvider peerProvider =
                (RMICacheManagerPeerProvider) cacheManager.getCacheManagerPeerProvider("RMI");

        // Not a clustered environment
        if (peerProvider == null)
            return;

        if (peerProvider instanceof DynamicRMICacheManagerPeerProvider) {
            DynamicRMICacheManagerPeerProvider dynamicPeerProvider = (DynamicRMICacheManagerPeerProvider) peerProvider;
            Collection<String> remoteHosts = dynamicPeerProvider.listRemoteHosts();
            for (String remoteHost : remoteHosts) {
                String rmiUrl = "//" + remoteHost + "/" + cacheName;
                logger.info("Unregistering remote cache : " + rmiUrl);
                dynamicPeerProvider.unregisterPeer(rmiUrl);
            }
        }
    }
}
