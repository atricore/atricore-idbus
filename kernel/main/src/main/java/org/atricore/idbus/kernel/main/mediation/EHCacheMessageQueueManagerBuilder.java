package org.atricore.idbus.kernel.main.mediation;

import net.sf.ehcache.CacheManager;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;

/**
 * Created by sgonzalez.
 */
public class EHCacheMessageQueueManagerBuilder extends MessageQueueManagerBuilder {

    private ArtifactGenerator artifactGenerator;

    private String name;

    private CacheManager cacheManager;

    public EHCacheMessageQueueManagerBuilder(ConfigurationContext config) throws MessageQueueFactoryConfigurationError {
        super(config);
    }

    @Override
    public MessageQueueManager buildMessageQueueManager() {
        EHCacheMessageQueueManager mqm = new EHCacheMessageQueueManager();

        mqm.setArtifactGenerator(artifactGenerator);
        mqm.setCacheManager(cacheManager);
        mqm.setName(name);

        String sRetryCount = getConfig().getProperty("binding.artifact.loadStateRetryCount");
        if (sRetryCount != null)
            mqm.setRetryCount(Integer.parseInt(sRetryCount));

        String sRetryDelay = getConfig().getProperty("binding.artifact.loadStateRetryDelay");
        if (sRetryDelay != null)
            mqm.setRetryDelay(Integer.parseInt(sRetryDelay));

        return mqm;

    }

    public ArtifactGenerator getArtifactGenerator() {
        return artifactGenerator;
    }

    public void setArtifactGenerator(ArtifactGenerator artifactGenerator) {
        this.artifactGenerator = artifactGenerator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

}
