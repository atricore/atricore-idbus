package org.atricore.idbus.bundles.ehcache.distribution.jms;

import net.sf.ehcache.distribution.jms.JMSUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jndi.ActiveMQInitialContextFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class EHCacheActiveMQInitialContextFactory extends ActiveMQInitialContextFactory {



    /**
     * Creates an initial context with
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Context getInitialContext(Hashtable environment) throws NamingException {

        Map<String, Object> data = new ConcurrentHashMap<String, Object>();

        String replicationTopicConnectionFactoryBindingName = (String) environment.get(JMSUtil.TOPIC_CONNECTION_FACTORY_BINDING_NAME);

        if (replicationTopicConnectionFactoryBindingName != null) {
            try {
                data.put(replicationTopicConnectionFactoryBindingName, createConnectionFactory(environment));
            } catch (URISyntaxException e) {
                throw new NamingException("Error initialisating TopicConnectionFactory with message " + e.getMessage());
            }
        }

        String getQueueConnectionfactoryBindingName = (String) environment.get(JMSUtil.GET_QUEUE_CONNECTION_FACTORY_BINDING_NAME);

        if (getQueueConnectionfactoryBindingName != null) {
            try {
                data.put(getQueueConnectionfactoryBindingName, createConnectionFactory(environment));
            } catch (URISyntaxException e) {
                throw new NamingException("Error initialisating TopicConnectionFactory with message " + e.getMessage());
            }
        }

        String replicationTopicBindingName = (String) environment.get(JMSUtil.REPLICATION_TOPIC_BINDING_NAME);
        if (replicationTopicBindingName != null) {
            data.put(replicationTopicBindingName, createTopic(replicationTopicBindingName));
        }


        String getQueueBindingName = (String) environment.get(JMSUtil.GET_QUEUE_BINDING_NAME);
        if (getQueueBindingName != null) {
            data.put(getQueueBindingName, createQueue(getQueueBindingName));
        }

        return createContext(environment, data);
    }

    @Override
    protected ActiveMQConnectionFactory createConnectionFactory(String name, Hashtable environment) throws URISyntaxException {

        ActiveMQConnectionFactory f = super.createConnectionFactory(name, environment);
        
        f.setDispatchAsync(false);
        f.setAlwaysSyncSend(true);
        f.setSendAcksAsync(false);
        f.setUseAsyncSend(false);
        f.setUseCompression(false);
        f.setSendTimeout(30000);

        return f;
    }
}
