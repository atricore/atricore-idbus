package org.atricore.idbus.bundles.apache.derby;

import org.apache.derby.drda.NetworkServerControl;

import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.net.InetAddress;
import java.io.PrintWriter;

import org.apache.commons.logging.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class OsgiNetworkServerControl implements InitializingBean, DisposableBean {



    private static Log logger = LogFactory.getLog(OsgiNetworkServerControl.class);

    private Map<String, NetworkServer> servers = new HashMap<String, NetworkServer>();

    private boolean running;

    public OsgiNetworkServerControl() {
        logger.debug("Creating Osgi based Derby Server Control component");
    }

    public void afterPropertiesSet() throws Exception {
        logger.info("Starting Apache Derby OSGi Network server control");
        running = true;

        for (NetworkServer server : servers.values()) {
            if (!server.isStarted()) {
                if (logger.isDebugEnabled())
                    logger.debug("Starting server " + server.getId());
                server.start();
            }
        }
    }

    public void destroy() throws Exception {
        logger.info("Starting Apache Derby OSGi Network server control");
        running = false;
        for (NetworkServer server : servers.values()) {
            if (server.isStarted()) {
                if (logger.isDebugEnabled())
                    logger.debug("Stoping server " + server.getId());
                server.stop();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void register(final NetworkServerDescriptor serverDescriptor, Map<String, ?> properties) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug("Registering Network Server Descriptor " + serverDescriptor);

        NetworkServer server = new NetworkServer(serverDescriptor.getPort() + "", serverDescriptor);
        this.servers.put(server.id, server);

        if (isRunning()) {
            if (logger.isDebugEnabled())
                logger.debug("Starting server " + server.getId());
            server.start();
        }
    }

    public void unregister(final NetworkServerDescriptor serverDescriptor, Map<String, ?> properties) throws Exception {
        if (logger.isDebugEnabled())
            logger.debug("Unregistering Network Server Descriptor " + serverDescriptor);

        NetworkServer server = servers.get(serverDescriptor.getPort() + "");

        if (logger.isDebugEnabled())
            logger.debug("Stoping server " + server.getId());
        server.stop();
    }

    class NetworkServer {

        private String id;

        private NetworkServerDescriptor descriptor;

        private NetworkServerControl derbyServer;

        private boolean started;

        NetworkServer(String id, NetworkServerDescriptor descriptor) throws Exception {
            this.id = id;
            this.descriptor = descriptor;

            InetAddress address = InetAddress.getByName("localhost");
            if (descriptor.getHostname() != null) {
                address = InetAddress.getByName(descriptor.getHostname());
            }

            derbyServer = new NetworkServerControl(address,
                    descriptor.getPort(),
                    descriptor.getUsername(),
                    descriptor.getPassword());

        }

        public void start() {
            try {
                started = true;
                logger.debug("Startup Derby Network Server..." + descriptor.getHostname() + ":" + descriptor.getPort());
                // TODO : Send a different OS ?
                derbyServer.start(new PrintWriter(System.err));
                logger.debug("Startup Derby Network Server...OK ");


                long timeout = descriptor.getTimeout() * 1000L;
                long now = System.currentTimeMillis();
                long expiration = now + timeout;

                {
                    // Try to check if connection is OK.
                    while (now < expiration) {

                        try {

                            try { synchronized (this) { wait(300); } } catch (InterruptedException ierr) {/**/}

                            // This will trigger a connection to the server ...
                            Properties p = derbyServer.getCurrentProperties();

                            if (logger.isDebugEnabled()) {
                                for (Object key : p.keySet()) {
                                    Object v = p.get(key);
                                    logger.debug(getId() + ":" + key + "=" + v);
                                }
                            }
                            return;

                        } catch (Exception e) {
                            logger.debug(e.getMessage());

                            if (now > expiration) {
                                logger.debug(e.getMessage(), e);
                                throw e;
                            }
                            // wait for a second and try again
                            try { synchronized (this) { wait(1000); } } catch (InterruptedException ierr) {/**/}
                        }
                        // Update time 
                        now = System.currentTimeMillis();

                    }
                }

                // Give up trying ... server is dead!

            } catch (Exception e) {
                logger.error("Cannot connect to DB Server " + e.getMessage(), e);
                throw new RuntimeException(e);
            }

        }

        public void stop() throws Exception {
            logger.debug("Stop Derby Network Server..." + descriptor.getHostname() + ":" + descriptor.getPort());
            started = false;
            derbyServer.shutdown();
        }

        public boolean isStarted() {
            return started;
        }

        public String getId() {
            return id;
        }
    }

}
