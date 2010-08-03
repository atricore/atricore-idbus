package org.atricore.idbus.kernel.main.mediation;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.jms.ConnectionFactory;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheMessageQueueManager implements MessageQueueManager, InitializingBean, DisposableBean {

    private static final Log logger = LogFactory.getLog(EHCacheMessageQueueManager.class);

    private ArtifactGenerator artifactGenerator;

    private String jmsProviderDestinationName;

    private Cache cache;

    private CacheManager cacheManager;

    private int receiveRetries = 5;

    public void destroy() throws Exception {
        shutDown();
    }

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public ConnectionFactory getConnectionFactory() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    public String getJmsProviderDestinationName() {
        return jmsProviderDestinationName;
    }

    public void setJmsProviderDestinationName(String jmsProviderDestinationName) {
        this.jmsProviderDestinationName = jmsProviderDestinationName;
    }

    public void init() throws Exception {

        if (cache == null) {

            if (!cacheManager.cacheExists(jmsProviderDestinationName)) {
                cacheManager.addCache(jmsProviderDestinationName);
            }
            
            cache = cacheManager.getCache(jmsProviderDestinationName);
        }
    }

    public Object pullMessage(Artifact artifact) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Pull Message for key: " + artifact.getContent());

        Element e = cache.get(artifact.getContent());

        if (e == null) {
            int retry = 0;
            while(e == null && retry <= receiveRetries) {

                logger.debug("Pull Message found NO message for [" + artifact + "]. Wait and retry ...");

                try { Thread.sleep(500); } catch (InterruptedException ie) { /*ignore it*/ }
                e = cache.get(artifact.getContent());
                retry ++;
            }

        }

        if (logger.isDebugEnabled())
            logger.debug("Pull Message found " + (e != null ? e.getValue() : "null") +
                    " content for artifact " + artifact.getContent());

        if (e != null) {
            cache.remove(artifact.getContent());


            return e.getValue();
        }

        return null;

    }

    public Object peekMessage(Artifact artifact) throws Exception {
        Element e = cache.get(artifact.getContent());
        return e.getValue();
    }

    public Artifact pushMessage(Object content) throws Exception {
        Artifact artifact = artifactGenerator.generate();

        if (logger.isDebugEnabled())
            logger.debug("Push Message for [" + artifact.getContent() + "]");

        Element e = new Element(artifact.getContent(), content);
        cache.put(e);
        return artifact;
    }

    public void shutDown() throws Exception {
        if (cacheManager.cacheExists(jmsProviderDestinationName)) {
            cacheManager.removeCache(jmsProviderDestinationName);
            cache = null;
        }
    }

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
}
