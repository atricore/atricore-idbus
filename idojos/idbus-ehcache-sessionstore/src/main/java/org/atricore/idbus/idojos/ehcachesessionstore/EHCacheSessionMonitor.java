package org.atricore.idbus.idojos.ehcachesessionstore;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.service.SSOSessionMonitor;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class EHCacheSessionMonitor implements SSOSessionMonitor, CacheEventListener,InitializingBean {

    private static final Log logger = LogFactory.getLog(EHCacheSessionMonitor.class);


    private String cacheName;

    private SSOSessionManager manager;

    public EHCacheSessionMonitor() {

    }

    public EHCacheSessionMonitor(SSOSessionManager manager) {
        this.manager = manager;
    }

    public void afterPropertiesSet() throws Exception {

    }

    public void start() {

    }

    public void stop() {

    }

    public void setInterval(long millis) {
        // Ignore this
    }

    public void notifyElementRemoved(Ehcache ehcache, Element e) throws CacheException {

    }

    public void notifyElementPut(Ehcache ehcache, Element e) throws CacheException {
    }

    public void notifyElementUpdated(Ehcache ehcache, Element e) throws CacheException {

    }

    public void notifyElementExpired(Ehcache ehcache, Element e) {

        if (!(e.getValue() instanceof BaseSession))
            return;

        BaseSession session = (BaseSession) e.getValue();
        if (logger.isDebugEnabled())
            logger.debug("Cache element expired, session ["+session.getUsername()+"]" + session.getId());

        manager.checkValidSessions(new BaseSession[]{session});

    }

    public void notifyElementEvicted(Ehcache ehcache, Element e) {

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

    public SSOSessionManager getManager() {
        return manager;
    }

    public void setManager(SSOSessionManager manager) {
        this.manager = manager;
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
