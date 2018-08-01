package org.atricore.idbus.kernel.main.mediation;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheMessageQueueManager implements MessageQueueManager, InitializingBean, DisposableBean {

    private static final Log logger = LogFactory.getLog(EHCacheMessageQueueManager.class);

    private ArtifactGenerator artifactGenerator;

    private String cacheName;

    private Cache cache;

    private CacheManager cacheManager;

    private int retryCount = 0;

    private int retryDelay = 100;

    @Override
    public void destroy() throws Exception {
        shutDown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }


    @Override
    public String getName() {
        return cacheName;
    }

    public void setName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void init() throws Exception {

        if (cache == null) {

            if (!cacheManager.cacheExists(cacheName)) {
                cacheManager.addCache(cacheName);
            }
            
            cache = cacheManager.getCache(cacheName);
        }
    }

    @Override
    public Object pullMessage(Artifact artifact) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Pull Message for key: " + artifact.getContent());

        Element e = cache.get(artifact.getContent());
        if (e == null) {

            // See if we must perform retries
            int retry = 0;
            while(e == null && retry < retryCount) {
                logger.debug("Pull Message found NO message for [" + artifact + "]. Wait and retry ...");
                try { Thread.sleep(retryDelay); } catch (InterruptedException ie) { /*ignore it*/ }
                e = cache.get(artifact.getContent());
                retry ++;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug("Pull Message found " + (e != null ? e.getValue() : "null") +
                    " content for artifact " + artifact.getContent());

        if (e != null) {
            cache.remove(artifact.getContent());
            return e.getObjectValue();
        }

        return null;

    }

    @Override
    public Object peekMessage(Artifact artifact) throws Exception {
        Element e = cache.get(artifact.getContent());

        if (e == null) {

            // See if we must perform retries
            int retry = 0;
            while(e == null && retry < retryCount) {
                logger.debug("Peek Message found NO message for [" + artifact + "]. Wait and retry ...");
                try { Thread.sleep(retryDelay); } catch (InterruptedException ie) { /*ignore it*/ }
                e = cache.get(artifact.getContent());
                retry ++;
            }

        }

        return e.getObjectValue();
    }

    @Override
    public Artifact pushMessage(Object content) throws Exception {
        Artifact artifact = artifactGenerator.generate();

        if (logger.isDebugEnabled())
            logger.debug("Push Message for [" + artifact.getContent() + "]");

        Element e = new Element(artifact.getContent(), content);
        cache.put(e);
        return artifact;
    }

    @Override
    public void shutDown() throws Exception {
        if (cacheManager.cacheExists(cacheName)) {
            cacheManager.removeCache(cacheName);
            cache = null;
        }
    }

    @Override
    public ArtifactGenerator getArtifactGenerator() {
        return artifactGenerator;
    }

    public void setArtifactGenerator(ArtifactGenerator artifactGenerator) {
        this.artifactGenerator = artifactGenerator;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }
}
