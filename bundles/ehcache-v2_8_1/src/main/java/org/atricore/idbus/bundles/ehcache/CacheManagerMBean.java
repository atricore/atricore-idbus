package org.atricore.idbus.bundles.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.distribution.CacheManagerPeerListener;
import net.sf.ehcache.distribution.CachePeer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Created by sgonzalez on 4/24/14.
 */
public class CacheManagerMBean {

    public static final String URL_DELIMITER = "|";

    private static final Log logger = LogFactory.getLog(CacheManagerMBean.class);

    private CacheManager cacheManager;

    public void removeALl(String cacheName) {
        cacheManager.getCache(cacheName).removeAll();
    }

    boolean removeQuiet(String cacheName, String key) throws java.lang.IllegalStateException {
        return cacheManager.getCache(cacheName).removeQuiet(key);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public boolean removeWithWriter(String cacheName, String key) throws java.lang.IllegalStateException, net.sf.ehcache.CacheException {
	    return cacheManager.getCache(cacheName).removeWithWriter(key);
	}

    public void removeAll(String cacheName) throws java.lang.IllegalStateException, net.sf.ehcache.CacheException{
        cacheManager.getCache(cacheName).removeAll();
	}

    public void removeAll(String cacheName, boolean doNotNotifyCacheReplicators) throws java.lang.IllegalStateException, net.sf.ehcache.CacheException{
        cacheManager.getCache(cacheName).removeAll(doNotNotifyCacheReplicators);
	}

    public void flush(String cacheName) throws java.lang.IllegalStateException, net.sf.ehcache.CacheException{
        cacheManager.getCache(cacheName).flush();
	}

    public int getSize(String cacheName) throws java.lang.IllegalStateException, net.sf.ehcache.CacheException{
        return cacheManager.getCache(cacheName).getSize();
	}

    public boolean hasAbortedSizeOf(String cacheName){
        return cacheManager.getCache(cacheName).hasAbortedSizeOf();
	}

    /**
     * Returns the number of elements in the memory store.
     *
     * @return the number of elements in the memory store
     * @throws IllegalStateException if the cache is not {@link net.sf.ehcache.Status#STATUS_ALIVE}
     */
    public long getMemoryStoreSize(String cacheName) throws IllegalStateException{
        return cacheManager.getCache(cacheName).getMemoryStoreSize();
    }

    /**
     * Returns the number of elements in the off-heap store.
     *
     * @return the number of elements in the off-heap store
     * @throws IllegalStateException if the cache is not {@link net.sf.ehcache.Status#STATUS_ALIVE}
     */
    public long getOffHeapStoreSize(String cacheName) throws IllegalStateException{
        return cacheManager.getCache(cacheName).getOffHeapStoreSize();
    }

    /**
     * Returns the number of elements in the disk store.
     *
     * @return the number of elements in the disk store.
     * @throws IllegalStateException if the cache is not {@link net.sf.ehcache.Status#STATUS_ALIVE}
     */
    public int getDiskStoreSize(String cacheName) throws IllegalStateException{
        return cacheManager.getCache(cacheName).getDiskStoreSize();
    }


    public String getStatus(String cacheName){
        net.sf.ehcache.Status s = cacheManager.getCache(cacheName).getStatus();
        return s.toString();
    }

    public boolean isElementInMemory(String cacheName, String key){
        return cacheManager.getCache(cacheName).isElementInMemory(key);
	}

    public boolean isElementOnDisk(String cacheName, String key){
        return cacheManager.getCache(cacheName).isElementOnDisk(key);
	}

    public java.lang.String getGuid(String cacheName){
        return cacheManager.getCache(cacheName).getGuid();
	}

    public void evictExpiredElements(String cacheName){
        cacheManager.getCache(cacheName).evictExpiredElements();
	}

    public boolean isKeyInCache(String cacheName, String key){
        return cacheManager.getCache(cacheName).isKeyInCache(key);
	}

    public boolean isValueInCache(String cacheName, String key){
        return cacheManager.getCache(cacheName).isValueInCache(key);
	}

    public String getLocalPeersUrls() {
        CacheManagerPeerListener cacheManagerPeerListener = cacheManager.getCachePeerListener("RMI");
        List localCachePeers = cacheManagerPeerListener.getBoundCachePeers();
        return assembleUrlList(localCachePeers);
    }

    protected String assembleUrlList(List localCachePeers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < localCachePeers.size(); i++) {
            CachePeer cachePeer = (CachePeer) localCachePeers.get(i);
            String rmiUrl = null;
            try {
                rmiUrl = cachePeer.getUrl();
            } catch (RemoteException e) {
                logger.error("This should never be thrown as it is called locally");
            }
            if (i != localCachePeers.size() - 1) {
                sb.append(rmiUrl).append(URL_DELIMITER);
            } else {
                sb.append(rmiUrl);
            }
        }

        return sb.toString();
    }
    
}
