/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.kernel.main.session.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.SecurityToken;
import org.atricore.idbus.kernel.main.session.*;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.session.exceptions.TooManyOpenSessionsException;
import org.atricore.idbus.kernel.main.store.session.SessionStore;
import org.atricore.idbus.kernel.main.util.ConfigurationContext;
import org.atricore.idbus.kernel.main.util.IDBusConfigurationConstants;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @org.apache.xbean.XBean element="session-manager"
 *
 * This is the default implementation of the SSO Session Manager.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SSOSessionManagerImpl.java 1331 2009-06-23 22:04:18Z sgonzalez $
 */

public class SSOSessionManagerImpl implements SSOSessionManager, InitializingBean, DisposableBean {

    private static final Log logger = LogFactory.getLog(SSOSessionManagerImpl.class);

    // Max inactive interval used for new sessions. Default is set to 30 minutes
    private int _maxInactiveInterval = 30;

    private int _maxSessionsPerUser = 1;

    private long _sessionMonitorInterval = 5000;

    private boolean _invalidateExceedingSessions = false;

    @Deprecated
    private String _securityDomainName;

    private String _node;

    private ConfigurationContext _config;

    // Some statistical information:

    private long _statsMaxSessions;

    private long _statsCreatedSessions;

    private long _statsDestroyedSessions;

    private long _statsCurrentSessions;

    private String _metricPrefix;

    private MonitoringServer _mServer;

    /**
     * This implementation uses a MemoryStore and a defaylt Session Id generator.
     */
    public SSOSessionManagerImpl() {
    }

    public SSOSessionManagerImpl(ConfigurationContext config) {
        _config = config;
    }

    public void afterPropertiesSet() throws Exception {
        initialize();
    }

    public void destroy() throws Exception {
        if (stpe != null) {
            try {
                stpe.shutdown();
            } catch (Exception e) {
                /* Ignore this*/
            }
        }
    }

    //-----------------------------------------------------
    // Instance variables :
    //-----------------------------------------------------

    private SessionStore _store;
    private SessionIdGenerator _idGen;

    // SSO Sessions monitor
    private SessionMonitor _monitor;

    private ScheduledThreadPoolExecutor stpe;

    //------------------------------------------------------
    // SSO Session Manager
    //------------------------------------------------------

    public void setSecurityDomainName(String securityDomainName) {
        _securityDomainName = securityDomainName;
    }

    public void setNode(String node) {
        _node = node;
    }


    /**
     * Initializes the manager.
     */
    public synchronized void initialize() {

        if (_config != null) {
            // Retrieve properties from configuration context
            _node = _config.getProperty(IDBusConfigurationConstants.IDBUS_NODE, _node);
        }

        logger.info("[initialize()] : IdGenerator.................=" + _idGen.getClass().getName());
        logger.info("[initialize()] : Store.......................=" + _store.getClass().getName());

        logger.info("[initialize()] : MaxInactive.................=" + _maxInactiveInterval);
        logger.info("[initialize()] : MaxSessionsPerUser..........=" + _maxSessionsPerUser);
        logger.info("[initialize()] : InvalidateExceedingSessions.=" + _invalidateExceedingSessions);
        logger.info("[initialize()] : SesisonMonitorInteval.......=" + _sessionMonitorInterval);
        logger.info("[initialize()] : Node........................=" + _node);
        logger.info("[initialize()] : Monitoring Server...........=" + (_mServer != null ? "FOUND" : "NOT FOUND"));

        // Start session monitor.


        _monitor = new SessionMonitor(this, getSessionMonitorInterval());

        stpe = new ScheduledThreadPoolExecutor(3);
        stpe.scheduleAtFixedRate(_monitor, getSessionMonitorInterval(),
                getSessionMonitorInterval(),
                TimeUnit.MILLISECONDS);

        // Register sessions in security domain !
        logger.info("[initialize()] : Restore Sec.Domain Registry.=" + _securityDomainName);

    }

    /**
     * TODO: deprecate
     * Initiates a new session.
     *
     * @return the new session identifier.
     * @deprecated
     */
    @Deprecated
    public String initiateSession(String username) throws SSOSessionException {
        throw new UnsupportedOperationException("Operation was deprecated!");
    }


    /**
     * Initiates a new session. The new session id is returned.
     *
     * @return the new session identifier.
     */
    public String initiateSession(String username, SecurityToken securityToken) throws SSOSessionException {

        // Invalidate sessions if necessary
        BaseSession sessions[] = _store.loadByUsername(username);

        // Check if we can open a new session for this user.
        if (!_invalidateExceedingSessions &&
                _maxSessionsPerUser != -1 &&
                _maxSessionsPerUser <= sessions.length) {
            throw new TooManyOpenSessionsException(sessions.length);
        }

        // Check if sessions should be auto-invalidated.
        if (_invalidateExceedingSessions && _maxSessionsPerUser != -1) {

            // Number of sessions to invalidate
            int invalidate = sessions.length - _maxSessionsPerUser + 1;
            if (logger.isDebugEnabled())
                logger.debug("Auto-invalidating " + invalidate + " sessions for user : " + username);

            for (int idx = 0; invalidate > 0; invalidate--) {
                BaseSession session = sessions[idx];

                if (logger.isDebugEnabled())
                    logger.debug("Auto-invalidating " + session.getId() + " session for user : " + username);

                invalidate(session.getId());
            }
        }

        try {
            this.accessSession(securityToken.getId());
            throw new IllegalArgumentException("SSO Session ID already in use : " + securityToken.getId());
        } catch (NoSuchSessionException e) { /* Normal behaviour */ }

        // Build the new session.
        BaseSession session = doMakeNewSession();

        // Configure the new session ...
        session.setId(securityToken.getId()); // We use the securityToken ID as session ID ...!
        session.setCreationTime(System.currentTimeMillis());
        session.setValid(true);
        session.setMaxInactiveInterval(getMaxInactiveInterval() * 60); // Convert minutes in seconds.
        session.setUsername(username);
        session.setSecurityToken(securityToken);
        session.setLastNode(_node);

        // Store the session
        _store.save(session);

        // Update statistics:

        // Number of created sessions
        _statsCreatedSessions ++;

        // Number of valid sessions (should match the store count!)
        _statsCurrentSessions ++;
        if (_mServer != null) {
            _mServer.recordMetric(_metricPrefix + ".SsoSessions", _statsCurrentSessions);
            _mServer.incrementCounter(_metricPrefix + ".SsoSessionsCreated");
        }

        // Max number of concurrent sessions
        if (_statsMaxSessions < _statsCurrentSessions) {
            _statsMaxSessions = _statsCurrentSessions;
            logger.info("Max concurrent SSO Sessions ["+_metricPrefix+"] " + _statsMaxSessions);
        }

        session.fireSessionEvent(BaseSession.SESSION_CREATED_EVENT, null);

        // Return its id.
        return session.getId();

    }

    /**
     * Gets an SSO session based on its id.
     *
     * @param sessionId the session id previously returned by initiateSession.
     * @throws NoSuchSessionException if the session id is not related to any sso session.
     */
    public SSOSession getSession(String sessionId) throws NoSuchSessionException, SSOSessionException {
        BaseSession s = _store.load(sessionId);
        if (s == null) {
            throw new NoSuchSessionException(sessionId);
        }
        return s;

    }

    /**
     * Gets an SecurityToken based on its SSO Session id
     *
     * @param sessionId the session id previously returned by initiateSession.
     * @throws NoSuchSessionException if the session id is not related to any sso session.
     */
    public SecurityToken getSecurityToken(String sessionId) throws NoSuchSessionException, SSOSessionException {
        SSOSession s = this.getSession(sessionId);
        return s.getSecurityToken();
    }

    /**
     * Gets all SSO sessions.
     */
    public Collection getSessions() throws SSOSessionException {
        return Arrays.asList(_store.loadAll());
    }

    /**
     * Gets an SSO session based on the associated user.
     *
     * @param username the username used when initiating the session.
     * @throws org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException
     *          if the session id is not related to any sso session.
     */
    public Collection getUserSessions(String username) throws NoSuchSessionException, SSOSessionException {
        BaseSession s[] = _store.loadByUsername(username);
        if (s.length < 1) {
            throw new NoSuchSessionException(username);
        }

        // Build the result
        List result = new ArrayList(s.length);
        for (int i = 0; i < s.length; i++) {
            result.add(s[i]);
        }

        return result;

    }

    /**
     * This method accesss the session associated to the received id.
     * This resets the session last access time and updates the access count.
     *
     * @param sessionId the session id previously returned by initiateSession.
     * @throws NoSuchSessionException if the session id is not valid or the session is not valid.
     */
    public void accessSession(String sessionId) throws NoSuchSessionException, SSOSessionException {


        try {

            if (logger.isTraceEnabled())
                logger.trace("[accessSession()] trying session : " + sessionId);

            // getSession will throw a NoSuchSessionException if not found.
            BaseSession s = (BaseSession) getSession(sessionId);
            if (!s.isValid()) {
                if (logger.isDebugEnabled())
                    logger.debug("[accessSession()] invalid session : " + sessionId);
                throw new NoSuchSessionException(sessionId);
            }

            s.access();
            s.setLastNode(_node);

            _store.save(s); // Update session information ...

            if (logger.isTraceEnabled())
                logger.trace("[accessSession()] ok");
        } finally {
            if (logger.isTraceEnabled())
                logger.trace("[accessSession()] ended for session : " + sessionId);


        }

    }

    /**
     * Invlalidates all open sessions.
     */
    public void invalidateAll() throws SSOSessionException {
        BaseSession[] sessions = _store.loadAll();
        for (BaseSession session : sessions) {
            // Mark session as expired (this will notify session listeners, if any)
            session.expire();
        }
    }

    /**
     * Invalidates a session.
     *
     * @param sessionId the session id previously returned by initiateSession.
     * @throws NoSuchSessionException if the session id is not related to any sso session.
     */
    public void invalidate(String sessionId) throws NoSuchSessionException, SSOSessionException {

        // Get current session.
        BaseSession s = (BaseSession) getSession(sessionId);

        // Remove it from the store
        try {


            _store.remove(sessionId);

            // Update statistics:
            // Number of created sessions
            _statsDestroyedSessions ++;

            // Number of valid sessions (should match the store count!)
            _statsCurrentSessions --;
            if (_mServer != null) {
                _mServer.recordMetric(_metricPrefix + ".SsoSessions", _statsCurrentSessions);
                _mServer.incrementCounter(_metricPrefix + ".SsoSessionsDestroyed");
            }

        } catch (SSOSessionException e) {
            logger.warn("Can't remove session from store: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Can't remove session from store\n" + e.getMessage(), e);
        }

        // Mark session as expired (this will notify session listeners, if any)
        s.expire(); // This will invalidate the session ...

    }

    /**
     * Check all sessions and remove those that are not valid from the store.
     * This method is invoked periodically to update sessions state.
     */
    public void checkValidSessions() {

        try {

            //---------------------------------------------
            // Verify invalid sessions ...
            //---------------------------------------------
            BaseSession sessions[] = _store.loadByValid(false);
            if (logger.isTraceEnabled())
                logger.trace("[checkValidSessions()] found " + sessions.length + " invalid sessions");

            checkValidSessions(sessions);

            //---------------------------------------------
            // Verify old sessions ...
            //---------------------------------------------

            // Convert Max Inactive Interval to MS
            long period = _maxInactiveInterval * 60L * 1000L;
            Date from = new Date(System.currentTimeMillis() - period);
            sessions = _store.loadByLastAccessTime(from);
            if (logger.isTraceEnabled())
                logger.trace("[checkValidSessions()] found " + sessions.length + " sessions last accessed before " + from);

            checkValidSessions(sessions);

        } catch (Exception e) {
            logger.error("Can't process expired sessions : " + e.getMessage(), e);
        }

    }

    protected void checkValidSessions(BaseSession[] sessions) {
        for (int i = 0; i < sessions.length; i++) {
            try {

                // Ignore valid sessions, they have not expired yet.
                BaseSession session = (BaseSession) sessions[i];

                // Only expire sessions handled by this node
                if (_node != null) {
                    String lastNode = session.getLastNode();
                    if (lastNode != null && !_node.equals(lastNode)) {
                        logger.trace("Session " + session.getId() + " is not handled by this node (" + _node + "/" + lastNode + ")");
                        continue;
                    }
                }

                if (!session.isValid()) {
                    // Remove invalid session from the store.
                    _store.remove(session.getId());

                    // Update statistics:
                    // Number of created sessions
                    _statsDestroyedSessions ++;

                    // Number of valid sessions (should match the store count!)
                    _statsCurrentSessions --;
                    if (_mServer != null) {
                        _mServer.recordMetric(_metricPrefix + ".SsoSessions", _statsCurrentSessions);
                        _mServer.incrementCounter(_metricPrefix + ".SsoSessionsDestroyed");
                    }

                    if (logger.isTraceEnabled())
                        logger.trace("[checkValidSessions()] Session expired : " + session.getId());
                }


            } catch (Exception e) {
                logger.warn("Can't remove session [" + i + "]; " + e.getMessage() != null ? e.getMessage() : e.toString(), e);
            }
        }

    }

    /**
     * @org.apache.xbean.Property alias="session-store"
     * @param ss
     */
    public void setSessionStore(SessionStore ss) {
        _store = ss;
    }

    /**
     * Dependency Injection of Session Id Generator.
     *
     * @org.apache.xbean.Property alias="session-id-generator"
     */
    public void setSessionIdGenerator(SessionIdGenerator g) {
        _idGen = g;
    }

    /**
     * Number of sessions registered in the manager.
     *
     * @return the number of sessions registered in this manager.
     */
    public int getSessionCount() throws SSOSessionException {
        return _store.getSize();
    }

    // ---------------------------------------------------------------
    // Properties
    // ---------------------------------------------------------------

    public int getMaxInactiveInterval() {
        return _maxInactiveInterval;
    }

    /**
     * @param maxInactiveInterval in minutes
     */
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        _maxInactiveInterval = maxInactiveInterval;
    }

    public int getMaxSessionsPerUser() {
        return _maxSessionsPerUser;
    }

    public void setMaxSessionsPerUser(int maxSessionsPerUser) {
        _maxSessionsPerUser = maxSessionsPerUser;
    }

    public boolean isInvalidateExceedingSessions() {
        return _invalidateExceedingSessions;
    }

    public void setInvalidateExceedingSessions(boolean invalidateExceedingSessions) {
        _invalidateExceedingSessions = invalidateExceedingSessions;
    }

    public long getSessionMonitorInterval() {
        return _sessionMonitorInterval;
    }

    public void setSessionMonitorInterval(long sessionMonitorInterval) {
        _sessionMonitorInterval = sessionMonitorInterval;
        if (_monitor != null) {
            _monitor.setInterval(_sessionMonitorInterval);
        }

    }

    public MonitoringServer getMonitoringServer() {
        return _mServer;
    }

    public void setMonitoringServer(MonitoringServer mServer) {
        this._mServer = mServer;
    }


    public String getMetricPrefix() {
        return _metricPrefix;
    }

    public void setMetricPrefix(String metricPrefix) {
        this._metricPrefix = metricPrefix;
    }


    // ---------------------------------------------------------------
    // Some stats
    // ---------------------------------------------------------------

    public long getStatsMaxSessions() {
        return _statsMaxSessions;
    }

    public long getStatsCreatedSessions() {
        return _statsCreatedSessions;
    }

    public long getStatsDestroyedSessions() {
        return _statsDestroyedSessions;
    }

    public long getStatsCurrentSessions() {
        return _statsCurrentSessions;
    }


    // ---------------------------------------------------------------
    // Protected utils.
    // ---------------------------------------------------------------

    /**
     * Get new session class to be used in the doLoad() method.
     */
    protected BaseSession doMakeNewSession() {
        return new BaseSessionImpl();
    }

    // ---------------------------------------------------------------
    // To expire threads periodically,
    // ---------------------------------------------------------------


}
