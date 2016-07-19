package org.atricore.idbus.kernel.main.mediation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;

import java.util.Properties;

/**
 * Created by sgonzalez.
 */
public abstract class MessageQueueManagerBuilder {

    private static Log logger = LogFactory.getLog(MessageQueueManagerBuilder.class);

    private ConfigurationContext config;

    public MessageQueueManagerBuilder(ConfigurationContext config) throws MessageQueueFactoryConfigurationError {
        this.config = config;
    }

    public abstract MessageQueueManager buildMessageQueueManager();

    public ConfigurationContext getConfig() {
        return config;
    }
}
