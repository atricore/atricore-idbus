package org.atricore.idbus.kernel.main.mediation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by sgonzalez.
 */
public class MessageQueueManagerFactory implements ApplicationContextAware {

    private static final Log logger = LogFactory.getLog(MessageQueueManagerFactory.class);

    private String selectedBuilder;

    private ApplicationContext appCtx;

    public MessageQueueManager build() {
        MessageQueueManagerBuilder builder = getBuilderInstance();
        return builder.buildMessageQueueManager();
    }

    public MessageQueueManagerBuilder getBuilderInstance() {
        Map<String, MessageQueueManagerBuilder> builders = appCtx.getBeansOfType(MessageQueueManagerBuilder.class);

        MessageQueueManagerBuilder anyBuilder = null;

        if (selectedBuilder == null) {
            logger.warn("No selected MessageQueueManagerBuilder specified, using defaults");
            for (Iterator<MessageQueueManagerBuilder> iterator = builders.values().iterator(); iterator.hasNext(); ) {
                MessageQueueManagerBuilder builder = iterator.next();

                if (builder instanceof MemoryMessageQueueManagerBuilder) {
                    logger.warn("Using MessageQueueManagerBuilder " + builder.getClass().getName());
                    return builder;
                }

                anyBuilder = builder;
            }

            if (anyBuilder != null)
                logger.warn("Using ANY MessageQueueManagerBuilder " + anyBuilder.getClass().getName());
            else
                logger.error("No MessageQueueManagerBuilder defined");

            return anyBuilder;

        }

        MessageQueueManagerBuilder builder = builders.get(selectedBuilder);
        if (builder == null) {
            logger.error("No MessageQueueManagerBuilder defined as bean '" + selectedBuilder + "'");
            throw new RuntimeException("No MessageQueueManagerBuilder defined as bean '" + selectedBuilder + "'");
        }

        return builder;
    }


    public void setSelectedBuilder(String selectedBuilder) {
        this.selectedBuilder = selectedBuilder;
    }

    public String getSelectedBuilder() {
        return selectedBuilder;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }
}
