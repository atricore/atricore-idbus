package org.atricore.idbus.bundles.ehcache.distribution;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.distribution.CachePeer;
import net.sf.ehcache.distribution.RMICacheManagerPeerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by sgonzalez on 12/26/15.
 */
public class DynamicRMICacheManagerPeerProvider extends RMICacheManagerPeerProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicRMICacheManagerPeerProvider.class.getName());

    protected Set<String> remoteHosts = new HashSet<String>();

    /**
     * Empty constructor.
     */
    public DynamicRMICacheManagerPeerProvider() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public final void init() {
        //nothing to do here
    }

    /**
     * Time for a cluster to form. This varies considerably, depending on the implementation.
     *
     * @return the time in ms, for a cluster to form
     */
    public long getTimeForClusterToForm() {
        return 0;
    }

    /**
     * Register a new peer.
     *
     * @param rmiUrl
     */
    public final synchronized void registerPeer(String rmiUrl) {
        peerUrls.put(rmiUrl, new Date());
    }


    /**
     * @return a list of {@link CachePeer} peers, excluding the local peer.
     */
    public final synchronized List listRemoteCachePeers(Ehcache cache) throws CacheException {
        List remoteCachePeers = new ArrayList();
        List staleList = new ArrayList();
        for (Iterator iterator = peerUrls.keySet().iterator(); iterator.hasNext(); ) {
            String rmiUrl = (String) iterator.next();
            String rmiUrlCacheName = extractCacheName(rmiUrl);

            if (!rmiUrlCacheName.equals(cache.getName())) {
                continue;
            }
            Date date = (Date) peerUrls.get(rmiUrl);
            if (!stale(date)) {
                CachePeer cachePeer = null;
                try {
                    cachePeer = lookupRemoteCachePeer(rmiUrl);
                    remoteCachePeers.add(cachePeer);
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Looking up rmiUrl " + rmiUrl + " through exception " + e.getMessage()
                                + ". This may be normal if a node has gone offline. Or it may indicate network connectivity"
                                + " difficulties", e);
                    }
                }
            } else {
                LOG.debug("rmiUrl {} should never be stale for a manually configured cluster.", rmiUrl);
                staleList.add(rmiUrl);
            }

        }

        //Remove any stale remote peers. Must be done here to avoid concurrent modification exception.
        for (int i = 0; i < staleList.size(); i++) {
            String rmiUrl = (String) staleList.get(i);
            peerUrls.remove(rmiUrl);
        }
        return remoteCachePeers;
    }

    public void registerHost(String remoteHost) {
        remoteHosts.add(remoteHost);
    }

    public Collection<String> listRemoteHosts() {
        return remoteHosts;
    }


    /**
     * Whether the entry should be considered stale.
     * <p/>
     * Manual RMICacheManagerProviders use a static list of urls and are therefore never stale.
     *
     * @param date the date the entry was created
     * @return true if stale
     */
    protected final boolean stale(Date date) {
        return false;
    }

    /**
     * Gets the cache name out of the url
     * @param rmiUrl
     * @return the cache name as it would appear in ehcache.xml
     */
    static String extractCacheName(String rmiUrl) {
        return rmiUrl.substring(rmiUrl.lastIndexOf('/') + 1);
    }



}