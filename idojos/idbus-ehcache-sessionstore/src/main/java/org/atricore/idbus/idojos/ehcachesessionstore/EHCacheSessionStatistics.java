package org.atricore.idbus.idojos.ehcachesessionstore;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.statistics.CacheUsageListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.service.SSOSessionStats;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EHCacheSessionStatistics implements CacheEventListener, SSOSessionStats, InitializingBean {

    private static final Log logger = LogFactory.getLog(EHCacheSessionStatistics.class);

    private MonitoringServer mServer;

    private String cacheName;
    
    private String metricsPrefix;
    
    private long currentSessions;
    
    private long maxSessions;
    
    private long createdSessions;

    private long destroyedSessions;

    public void afterPropertiesSet() throws Exception {

    }

    public String getMetricsPrefix() {
        return metricsPrefix;
    }

    public void setMetricsPrefix(String metricsPrefix) {
        this.metricsPrefix = metricsPrefix;
    }

    public MonitoringServer getMonitoringServer() {
        return mServer;
    }

    public void setMonitoringServer(MonitoringServer _mServer) {
        this.mServer = _mServer;
    }

    public long getMaxSessions() {
        return maxSessions;
    }

    public long getCreatedSessions() {
        return createdSessions;
    }

    public long getDestroyedSessions() {
        return destroyedSessions;
    }

    public long getCurrentSessions() {
        return currentSessions;
    }

    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {

        if (!(element.getValue() instanceof BaseSession))
            return;

        // Update statistics:
        // Number of destroyed sessions
        destroyedSessions ++;

        // Number of valid sessions (should match the store count!)
        currentSessions --;

        mServer.recordMetric(metricsPrefix + "/SsoSessions/Total", currentSessions);
        mServer.incrementCounter(metricsPrefix + "/SsoSessions/Destroyed");

        if (logger.isTraceEnabled())
            logger.trace("Total sessions [" + cacheName + "] " + currentSessions);

    }

    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {

        if (!(element.getValue() instanceof BaseSession))
            return;

        // Number of created sessions
        createdSessions ++;

        // Number of valid sessions (should match the store count!)
        currentSessions ++;

        // Max number of concurrent sessions
        if (maxSessions < currentSessions) {
            maxSessions = currentSessions;

            logger.info("Max concurrent SSO Sessions ["+ cacheName +"] " + maxSessions);
        }

        mServer.recordMetric(metricsPrefix + "/SsoSessions/Total", currentSessions);
        mServer.incrementCounter(metricsPrefix + "/SsoSessions/Created");

        if (logger.isTraceEnabled())
            logger.trace("Total sessions [" + cacheName + "] " + currentSessions);


    }

    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {

    }

    public void notifyElementExpired(Ehcache ehcache, Element element) {

        if (!(element.getValue() instanceof BaseSession))
            return;

        // Update statistics:
        // Number of destroyed sessions
        destroyedSessions ++;

        // Number of valid sessions (should match the store count!)
        currentSessions --;

        mServer.recordMetric(metricsPrefix + "/SsoSessions/Total", currentSessions);
        mServer.incrementCounter(metricsPrefix + "/SsoSessions/Destroyed");

        if (logger.isTraceEnabled())
            logger.trace("Total sessions [" + cacheName + "] " + currentSessions);

    }

    public void notifyElementEvicted(Ehcache ehcache, Element element) {

    }

    public void notifyRemoveAll(Ehcache ehcache) {

    }

    public void dispose() {

    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // to shut up check-style, why do we need this ?
        super.clone();
        throw new CloneNotSupportedException();
    }

}
