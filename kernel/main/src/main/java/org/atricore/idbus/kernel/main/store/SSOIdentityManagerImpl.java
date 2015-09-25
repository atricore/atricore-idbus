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

package org.atricore.idbus.kernel.main.store;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.BaseUser;
import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.session.BaseSession;
import org.atricore.idbus.kernel.main.session.SSOSessionManager;
import org.atricore.idbus.kernel.main.session.exceptions.NoSuchSessionException;
import org.atricore.idbus.kernel.main.session.exceptions.SSOSessionException;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.IdentityStore;
import org.atricore.idbus.kernel.main.store.identity.IdentityStoreKeyAdapter;
import org.atricore.idbus.kernel.monitoring.core.MonitoringServer;

/**
 * @org.apache.xbean.XBean element="identity-manager"
 *
 * This is the default implementation of an SSOIdentityManager.
 * This implementation keeps track of user and session associations in memory.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SSOIdentityManagerImpl.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public class SSOIdentityManagerImpl implements SSOIdentityManager {

    private static final Log logger = LogFactory.getLog(SSOIdentityManagerImpl.class);

    private MonitoringServer mServer;

    // Identity store used by the manager.
    private IdentityStore _store;
    private IdentityStoreKeyAdapter _keyAdapter;
    private SSOSessionManager _sessionManager;

    private String metricPrefix;

    private String metricBase = "/Store/Transactions";

    /**
     *
     */
    public SSOIdentityManagerImpl() {
    }

    /**
     * Finds a user based on its name.
     *
     * @param name the user login name, wich is unique for a domain.
     * @throws NoSuchUserException if the user does not exist for the domain.
     */
    public SSOUser findUser(String name)
            throws NoSuchUserException, SSOIdentityException {

        long s = System.currentTimeMillis();
        String metric = metricPrefix + metricBase + "/findUser";

        try {


            // Find user in store
            UserKey key = getIdentityStoreKeyAdapter().getKeyForUsername(name);
            BaseUser user = getIdentityStore().loadUser(key);
            if (user == null)
                throw new NoSuchUserException(key);

            // Done ... user found.
            return user;
        } finally {
            long e = System.currentTimeMillis();
            if (mServer != null)
                mServer.recordResponseTimeMetric(metric, e - s);
        }
    }

    /**
     * Finds the user associated to a sso session
     *
     * @param sessionId the sso session identifier
     * @throws SSOIdentityException if no user is associated to this session id.
     */
    public SSOUser findUserInSession(String sessionId)
            throws SSOIdentityException {

        BaseUser user = null;
        UserKey key = null;

        long s = System.currentTimeMillis();
        String metric = metricPrefix + metricBase + "/findUserInSession";

        try {
            BaseSession session = (BaseSession) getSessionManager().getSession(sessionId);
            key = new SimpleUserKey(session.getUsername());
            user = getIdentityStore().loadUser(key);

            if (logger.isDebugEnabled())
                logger.debug("[findUserInSession(" + sessionId + ")] Found :  " + user);

            return user;

        } catch (NoSuchSessionException e) {
            throw new SSOIdentityException("Invalid session : " + sessionId);

        } catch (SSOSessionException e) {
            throw new SSOIdentityException(e.getMessage(), e);
        } finally {
            long e = System.currentTimeMillis();
            if (mServer != null)
                mServer.recordResponseTimeMetric(metric, e - s);

        }

    }


    /**
     * Finds a collection of user's roles.
     * Elements in the collection are SSORole instances.
     *
     * @param username
     * @throws SSOIdentityException
     */
    public SSORole[] findRolesByUsername(String username)
            throws SSOIdentityException {

        long s = System.currentTimeMillis();
        String metric = metricPrefix + metricBase + "/findRolesByUsername";

        try {
            UserKey key = getIdentityStoreKeyAdapter().getKeyForUsername(username);
            return getIdentityStore().findRolesByUserKey(key);
        } finally {
            long e = System.currentTimeMillis();
            if (mServer != null)
                mServer.recordResponseTimeMetric(metric, e - s);
        }
    }

    /**
     * Checks if current user exists in this manager.
     *
     * @throws NoSuchUserException  if the user does not exists.
     * @throws SSOIdentityException if an error occurs
     */
    public void userExists(String username) throws NoSuchUserException, SSOIdentityException {
        long s = System.currentTimeMillis();
        String metric = metricPrefix + metricBase + "/userExists";

        try {
            UserKey key = getIdentityStoreKeyAdapter().getKeyForUsername(username);
            if (!getIdentityStore().userExists(key))
                throw new NoSuchUserException(key);
        } finally {
            long e = System.currentTimeMillis();
            if (mServer != null)
                mServer.recordResponseTimeMetric(metric, e - s);

        }
    }


    // --------------------------------------------------------------------
    // Public utils
    // --------------------------------------------------------------------

    public SSOSessionManager getSessionManager() {
        return _sessionManager;
    }

    public void set_sessionManager(SSOSessionManager sessionManager) {
        this._sessionManager = sessionManager;
    }

    /**
     * Used to set the store for this manager.
     *
     * @param s
     */
    public void setIdentityStore(IdentityStore s) {
        _store = s;
    }

    public void setIdentityStoreKeyAdapter(IdentityStoreKeyAdapter a) {
        _keyAdapter = a;
    }

    public void initialize() {

    }

    public IdentityStore getIdentityStore() {
        return _store;
    }

    public IdentityStoreKeyAdapter getIdentityStoreKeyAdapter() {
        return _keyAdapter;
    }

    public MonitoringServer getMonitoringServer() {
        return mServer;
    }

    public void setMonitoringServer(MonitoringServer mServer) {
        this.mServer = mServer;
    }

    public String getMetricPrefix() {
        return metricPrefix;
    }

    public void setMetricPrefix(String metricPrefix) {
        this.metricPrefix = metricPrefix;
    }
}
