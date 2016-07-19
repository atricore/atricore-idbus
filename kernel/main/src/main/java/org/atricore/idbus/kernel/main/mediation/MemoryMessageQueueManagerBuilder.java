
package org.atricore.idbus.kernel.main.mediation;

import org.atricore.idbus.kernel.main.util.ConfigurationContext;

import java.util.Properties;

/**
 * Created by sgonzalez.
 */
public class MemoryMessageQueueManagerBuilder extends MessageQueueManagerBuilder {

    private int artifactTTL = 60 * 60; // 1hr, in seconds

    private int monitorInterval = 60; // 1 min, in seconds

    public MemoryMessageQueueManagerBuilder(ConfigurationContext config) throws MessageQueueFactoryConfigurationError {
        super(config);
    }


    @Override
    public MessageQueueManager buildMessageQueueManager() {
        MemoryMessageQueueManager mqm = new MemoryMessageQueueManager();

        mqm.setArtifactTTL(artifactTTL);
        mqm.setMonitorInterval(monitorInterval);

        return mqm;
    }

    public int getArtifactTTL() {
        return artifactTTL;
    }

    public void setArtifactTTL(int artifactTTL) {
        this.artifactTTL = artifactTTL;
    }

    public int getMonitorInterval() {
        return monitorInterval;
    }

    public void setMonitorInterval(int monitorInterval) {
        this.monitorInterval = monitorInterval;
    }
}
