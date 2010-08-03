package org.ops4j.pax.web.service.jetty.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.SessionHandler;
import org.ops4j.pax.web.service.jetty.internal.JettyServerWrapper;
import org.ops4j.pax.web.service.jetty.spi.SessionHandlerBuilder;
import org.ops4j.pax.web.service.spi.model.Model;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheSessionHandlerBuilder implements SessionHandlerBuilder {

    private static final Log LOG = LogFactory.getLog( EHCacheSessionHandlerBuilder.class );

    private CacheManager cacheManager;

    private Cache sessionsCache;

    private String cacheName;

    public EHCacheSessionHandlerBuilder() {
        LOG.debug("Using EHCache Session Handler builder ...");
        cacheName = "idbus-http-sessions";
    }

    protected void doStart() {

        if (!cacheManager.cacheExists(cacheName)) {
            org.mortbay.log.Log.info("Adding new Sessions EHCache cache " + cacheName);
            cacheManager.addCache(cacheName);
        }

        this.sessionsCache = cacheManager.getCache(cacheName);

    }

    protected void doStop() {
        if (sessionsCache != null) {
            cacheManager.removeCache(sessionsCache.getName());
            sessionsCache = null;
        }
    }

    public SessionHandler build(Server server, Model model) {
        LOG.debug("Creating EHCache Session Manager");

        // Only one cache for all apps:
        if (org.mortbay.log.Log.isDebugEnabled())
            org.mortbay.log.Log.debug("Building EHCache SessionHandler w/cache " + cacheName);

        // Sessin Id Manager
        EHCacheSessionIdManager sessionIdManager = new EHCacheSessionIdManager(server, sessionsCache);
        String workerName = model.getContextModel().getSessionWorkerName();
        if (workerName != null && !workerName.equals("") && !workerName.equals("null")) {
            sessionIdManager.setWorkerName(workerName);
        } else {
            sessionIdManager.setWorkerName(null);
        }

        if (LOG.isDebugEnabled())
            LOG.debug("Using EHCache Session ID Manager for worker : " +
                    workerName);

        // Session Manager
        EHCacheSessionManager sm = new EHCacheSessionManager(server, model, sessionsCache);
        sm.setIdManager(sessionIdManager);

        //Session Handler
        return new EHCacheSessionHandler(sm);
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

}
