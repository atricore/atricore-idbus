package org.atricore.idbus.bundles.ehcache.distribution;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.*;
import net.sf.ehcache.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class IdBusRMICacheManagerPeerProviderFactory extends RMICacheManagerPeerProviderFactory {

    private static final Logger LOG = LoggerFactory.getLogger(IdBusRMICacheManagerPeerProviderFactory.class.getName());

    private static final String HOST_NAME = "hostName";
    private static final String PEER_DISCOVERY = "peerDiscovery";
    private static final String AUTOMATIC_PEER_DISCOVERY = "automatic";
    private static final String MANUALLY_CONFIGURED_PEER_DISCOVERY = "manual";
    private static final String DYNAMICALLY_CONFIGURED_PEER_DISCOVERY = "dynamic";
    private static final String RMI_URLS = "rmiUrls";
    private static final String REMOTE_HOSTS = "remoteHosts";
    private static final String MULTICAST_GROUP_PORT = "multicastGroupPort";
    private static final String MULTICAST_GROUP_ADDRESS = "multicastGroupAddress";
    private static final String MULTICAST_PACKET_TTL = "timeToLive";
    private static final int MAXIMUM_TTL = 255;
    private static final String URL_DELIMITER = "|";

    /**
     * @param properties implementation specific properties. These are configured as comma
     *                   separated name value pairs in ehcache.xml
     */
    public CacheManagerPeerProvider createCachePeerProvider(CacheManager cacheManager, Properties properties)
            throws CacheException {
        String peerDiscovery = PropertyUtil.extractAndLogProperty(PEER_DISCOVERY, properties);
        if (peerDiscovery == null || peerDiscovery.equalsIgnoreCase(AUTOMATIC_PEER_DISCOVERY)) {
            try {
                return createAutomaticallyConfiguredCachePeerProvider(cacheManager, properties);
            } catch (IOException e) {
                throw new CacheException("Could not create CacheManagerPeerProvider. Initial cause was " + e.getMessage(), e);
            }
        } else if (peerDiscovery.equalsIgnoreCase(MANUALLY_CONFIGURED_PEER_DISCOVERY)) {
            return createManuallyConfiguredCachePeerProvider(properties);
        } if (peerDiscovery.equalsIgnoreCase(DYNAMICALLY_CONFIGURED_PEER_DISCOVERY)) {
            return createDynamicallyConfiguredCachePeerProvider(properties);
        } else {
            return null;
        }
    }

    /**
     * peerDiscovery=dynamic, remoteHosts=hostname:port hostname:port hostname:port
     */
    protected CacheManagerPeerProvider createDynamicallyConfiguredCachePeerProvider(Properties properties) {
        String remoteHosts = PropertyUtil.extractAndLogProperty(REMOTE_HOSTS, properties);
        if (remoteHosts == null || remoteHosts.length() == 0) {
            LOG.info("Starting dynamic peer provider with empty list of hosts. " +
                    "No replication will occur unless hosts are added.");
            remoteHosts = "";
        }
        remoteHosts = remoteHosts.trim();
        StringTokenizer stringTokenizer = new StringTokenizer(remoteHosts, URL_DELIMITER);
        DynamicRMICacheManagerPeerProvider rmiPeerProvider = new DynamicRMICacheManagerPeerProvider();
        while (stringTokenizer.hasMoreTokens()) {
            String remoteHost = stringTokenizer.nextToken();
            remoteHost = remoteHost.trim();
            rmiPeerProvider.registerHost(remoteHost);

            LOG.debug("Registering host {}", remoteHost);
        }
        return rmiPeerProvider;
    }

}
