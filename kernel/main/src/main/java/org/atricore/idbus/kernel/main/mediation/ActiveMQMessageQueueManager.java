package org.atricore.idbus.kernel.main.mediation;

import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.activemq.util.ByteSequence;
import org.apache.activemq.util.JMSExceptionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.common.support.osgi.OsgiBundlespaceClassLoader;
import org.atricore.idbus.kernel.main.mediation.osgi.ClassLoadingAwareObjectInputStream;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.osgi.context.BundleContextAware;

import javax.jms.*;
import java.io.*;
import java.util.Enumeration;
import java.util.zip.InflaterInputStream;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class ActiveMQMessageQueueManager implements MessageQueueManager, BundleContextAware, InitializingBean, DisposableBean {

    private static transient Log log = LogFactory.getLog(MessageQueueManager.class);

    private ArtifactGenerator artifactGenerator;

    private ConnectionFactory connectionFactory;
    private String jmsProviderDestinationName;
    private Connection connection;
    private Session session;
    private Queue queue;
    private boolean initialized = false;

    private BundleContext bundleContext;
    private ClassLoader osgiClassLoader;

    private long receiveTimeout = 30000L;

    private long artifactTTL = 600L;

    public void afterPropertiesSet() throws Exception {
        init();
    }

    public void destroy() throws Exception {
        shutDown();
    }

    public void setBundleContext(BundleContext bundleContext) {

        this.bundleContext = bundleContext;
        if (log.isDebugEnabled())
            log.debug("Creating OSGi Bundlespace classloader using bundle " +
                    bundleContext.getBundle().getSymbolicName() + ":" +
                    bundleContext.getBundle().getBundleId());

        osgiClassLoader = new OsgiBundlespaceClassLoader(
            bundleContext,
            Thread.currentThread().getContextClassLoader(),
            bundleContext.getBundle());

    }

    /**
     * @org.apache.xbean.Property alias="connection-factory"
     */
    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * @org.apache.xbean.Property alias="jms-provider-destination"
     */
    public String getJmsProviderDestinationName() {
        return jmsProviderDestinationName;
    }

    public void setJmsProviderDestinationName(String jmsProviderDestinationName) {
        this.jmsProviderDestinationName = jmsProviderDestinationName;
    }

    public void init() throws Exception {

        if (initialized)
            return;

        synchronized (this) {

            if (initialized)
                return;

            log.debug("Initializing Message Queue Manager ... ");

            connection = connectionFactory.createConnection();
            connection.start();

            log.trace("ReceiveTimeout:" + receiveTimeout);
            log.trace("Artifact time to live: " + artifactTTL);

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(jmsProviderDestinationName);
            initialized = true;

            log.info("Initializing Message Queue Manager ... OK");

        }
    }

    public void shutDown() throws Exception {

        if (!initialized)
            return;

        try {
            session.close();
            session = null;
        } finally {
            try {
                connection.stop();
                connection.close();
                connection = null;
            } catch (Exception e) {
                /**/
            }
            initialized = false;
        }
    }

    public Object pullMessage(Artifact artifact) throws Exception {

        init();

        Object content = null;
        MessageConsumer consumer;

        String qry = "artifact = '" + artifact.getContent() + "'";
        log.debug("Pulling Message with Selector ["+qry+"]");

        consumer = session.createConsumer(queue, qry);

        try {

            Message message = pullMessageSync(artifact, consumer);

            if (message == null) {
                synchronized(this) {
                    try {
                        wait(100);
                        message = pullMessageSync(artifact, consumer);
                    } catch (InterruptedIOException e) {
                        /* */
                    }
                }
            }

            // Workaround for OSGi classloader issues
            ActiveMQObjectMessage amqMsg = (ActiveMQObjectMessage) message;


            if (message == null) {
                log.warn("No message received for ["+qry+"]");
                return null;
            }

            if (amqMsg.getProperty("artifact") == null) {
                throw new IdentityMediationException("Message does not contain 'artifact' property " + amqMsg);
            }

            if (!amqMsg.getProperty("artifact").equals(artifact.getContent())) {
                throw new IdentityMediationException("Message 'artifact' invalid (received:" +
                        amqMsg.getProperty("artifact") + ", expected:" + artifact.getContent() + ")");
            }

            content = getObject(amqMsg);
            
        } finally {
            if (consumer != null) {
                consumer.close();
            }
        }

        return content;

    }

    protected Message pullMessageSync(Artifact artifact, MessageConsumer consumer) throws Exception {

        ObjectMessage message = (ObjectMessage) consumer.receive(receiveTimeout);

//        if (message == null) {
//            int retry = 0;
//            while(message == null && retry <= receiveRetries) {
//                log.debug("Pull Message found NO message for [" + artifact + "]. Wait and retry ...");
//                try { Thread.sleep(500); } catch (InterruptedException ie) { /*ignore it*/ }
//                message = (ObjectMessage) consumer.receive(receiveTimeout);
//                retry ++;
//            }
//        }

        if (message != null) {
            log.debug("Pull Message found message " + message);
        } else {
            log.debug("Pull Message found NO message for " + artifact);
        }

        return message;
    }


    public Object peekMessage(Artifact artifact) throws Exception {
        init();

        Object result = null;
        String qry = "artifact = '" + artifact.getContent() + "'";
        log.debug("Peeking message for ["+qry+"]");
        QueueBrowser browser = session.createBrowser(queue, qry);
        Enumeration elems = browser.getEnumeration();

        if(elems.hasMoreElements()){
            result = getObject((ActiveMQObjectMessage)elems.nextElement());
        }

        if (log.isDebugEnabled())
            log.debug("Peek Message found " + (result != null ? result.getClass().getName() : "null") +
                    " content for artifact " + artifact.getContent());

        return result;
    }

    public Artifact pushMessage(Object content) throws Exception {

        if (content == null)
            throw new NullPointerException("Message content cannot be null");

        if (!(content instanceof Serializable))
            throw new NotSerializableException(content.getClass().getName());

        init();

        MessageProducer producer = session.createProducer(queue);
        ObjectMessage message = session.createObjectMessage((Serializable)content);

        Artifact artifact = artifactGenerator.generate();
        if (log.isDebugEnabled())
            log.debug("Push Message "+content.getClass().getName()+" for artifact " + artifact.getContent());

        message.setStringProperty("artifact", artifact.getContent());
        producer.setTimeToLive(artifactTTL);
        producer.send(message);

        return artifact;
    }

    /**
     * In OSGi, we need a differente way to unmarshall objects!
     */
    protected Object getObject(ActiveMQObjectMessage msg) throws JMSException {

        Object object;

        try {
            ByteSequence content = msg.getContent();
            InputStream is = new ByteArrayInputStream(content);
            if (msg.isCompressed()) {
                is = new InflaterInputStream(is);
            }
            DataInputStream dataIn = new DataInputStream(is);
            ClassLoadingAwareObjectInputStream objIn = new ClassLoadingAwareObjectInputStream(osgiClassLoader, dataIn);
            try {
                object = (Serializable)objIn.readObject();
            } catch (ClassNotFoundException ce) {
                log.error(ce.getMessage(), ce);
                throw new IOException("Cannot read Object:" + ce.getMessage());
            }
            dataIn.close();
        } catch (IOException e) {
            throw JMSExceptionSupport.create("Failed to build body from bytes. Reason: " + e, e);
        }

        return object;

    }

    /**
     * @org.apache.xbean.Property alias="artifact-generator"
     */
    public ArtifactGenerator getArtifactGenerator() {
        return artifactGenerator;
    }

    public void setArtifactGenerator(ArtifactGenerator artifactGenerator) {
        this.artifactGenerator = artifactGenerator;
    }

    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public long getArtifactTTL() {
        return artifactTTL;
    }

    public void setArtifactTTL(long artifactTTL) {
        this.artifactTTL = artifactTTL;
    }
}