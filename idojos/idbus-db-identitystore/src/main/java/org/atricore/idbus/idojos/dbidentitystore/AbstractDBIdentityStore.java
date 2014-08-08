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
package org.atricore.idbus.idojos.dbidentitystore;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.store.AbstractStore;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.authn.*;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * DB implementation of an IdentityStore and CredentialStore.
 * Three querys have to be configured to the store :
 * <p/>
 * - UserQueryString : Used to validate user existence = "SELECT MY_USER FROM MY_USER_TABLE WHERE MY_USR = '?'"
 * - RolesQueryString : Used to retrieve user's roles = "SELECT MY_ROLE FROM MY_USER_ROLES_TABLE WHERE MY_USER = '?'";
 * - CredentialQueryString : Used to retrieve known credentials for a user.
 * This query depends on the configured authentication scheme.  For a user / password based scheme the query could be
 * "SELECT MY_USER AS USERNAME, MY_PWD AS PASSWORD FROM MY_USER_TABLE WHERE MY_USER ='?';
 * The alias is important as is used to map the retrieved value to a specific credential type.
 * <p/>
 * Subclasses have to implement getDBConnection() method, this allows jdbc/datasource based stores.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AbstractDBIdentityStore.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public abstract class AbstractDBIdentityStore extends AbstractStore {

    private static final Log logger = LogFactory.getLog(AbstractDBIdentityStore.class);

    private String _userQueryString;
    private String _rolesQueryString;
    private String _credentialsQueryString;
    private String _userPropertiesQueryString;
    private String _resetCredentialDml;
    private String _relayCredentialQueryString;
    private boolean _useColumnsAsPropertyNames;

    // ---------------------------------------------------------------
    // AbstractStore extension.
    // ---------------------------------------------------------------

    public BaseUser loadUser(UserKey key) throws NoSuchUserException, SSOIdentityException {
        Connection c = null;
        try {
            if (!(key instanceof SimpleUserKey)) {
                throw new SSOIdentityException("Unsupported key type : " + key.getClass().getName());
            }

            c = getDBConnection();
            IdentityDAO dao = getIdentityDAO(c);
            BaseUser user = dao.selectUser((SimpleUserKey) key);

            // Optionally find user properties.
            if (getUserPropertiesQueryString() != null) {
                SSONameValuePair[] props = dao.selectUserProperties((SimpleUserKey) key);
                user.setProperties(props);
            }

            return user;

        } finally {
            closeDBConnection(c);
        }
    }

    public BaseRole[] findRolesByUserKey(UserKey key) throws SSOIdentityException {
        Connection c = null;
        try {
            if (!(key instanceof SimpleUserKey)) {
                throw new SSOIdentityException("Unsupported key type : " + key.getClass().getName());
            }

            c = getDBConnection();
            IdentityDAO dao = getIdentityDAO(c);
            BaseRole[] roles = dao.selectRolesByUserKey((SimpleUserKey) key);
            return roles;

        } finally {
            closeDBConnection(c);
        }
    }

    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {
        Connection c = null;
        try {
            if (!(key instanceof SimpleUserKey)) {
                throw new SSOIdentityException("Unsupported key type : " + key.getClass().getName());
            }

            c = getDBConnection();
            IdentityDAO dao = getIdentityDAO(c, cp);
            Credential[] credentials = dao.selectCredentials((SimpleUserKey) key);
            return credentials;

        } finally {
            closeDBConnection(c);
        }

    }

    // -----------------------------------------------------------------------------------
    // Protected utils.
    // -----------------------------------------------------------------------------------

    /**
     * Subclasses must implement getDBConnection() method.
     */
    public abstract Connection getDBConnection() throws SSOIdentityException;

    protected IdentityDAO getIdentityDAO(Connection c, CredentialProvider cp) {

        return new IdentityDAO(c,
                cp,
                getUserQueryString(),
                getRolesQueryString(),
                getCredentialsQueryString(),
                getUserPropertiesQueryString(),
                getResetCredentialDml(),
                getRelayCredentialQueryString(),
                isUseColumnsAsPropertyNames());
    }

    protected IdentityDAO getIdentityDAO(Connection c) {

        return new IdentityDAO(c,
                null,
                getUserQueryString(),
                getRolesQueryString(),
                getCredentialsQueryString(),
                getUserPropertiesQueryString(),
                getResetCredentialDml(),
                getRelayCredentialQueryString(),
                isUseColumnsAsPropertyNames());
        
    }

    /**
     * Close the given db connection.
     *
     * @param dbConnection
     * @throws SSOIdentityException
     */
    protected void closeDBConnection(Connection dbConnection) throws SSOIdentityException {

        try {
            if (dbConnection != null && !dbConnection.isClosed()) {
                try {
                    dbConnection.commit();
                } catch (SQLException e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Error while committing connection");
                    }
                    try {
                        dbConnection.rollback();
                    } catch (SQLException e1) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Error while rollback connection");
                        }
                    }
                }
            }
            if (dbConnection != null && !dbConnection.isClosed()) {
                dbConnection.close();
            }
        }
        catch (SQLException se) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error while clossing connection");
            }

            throw new SSOIdentityException(
                    "Error while clossing connection\n" + se.getMessage());
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error while clossing connection");
            }

            throw new SSOIdentityException(
                    "Error while clossing connection\n" + e.getMessage());

        }

    }

    // ---------------------------------------------------------------
    // Configuration properties.
    // ---------------------------------------------------------------

    /**
     * The SQL query that returns a user name based on a user key.
     */
    public String getUserQueryString() {
        return _userQueryString;
    }

    /**
     * The SQL query that returns the list of roles associated with a given user.
     */
    public String getRolesQueryString() {
        return _rolesQueryString;
    }

    /**
     * The SQL query that returns the list of known credentials associated with a given user.
     */
    public String getCredentialsQueryString() {
        return _credentialsQueryString;
    }

    /**
     * The SQL query that returns the list of properties associated with a given user.
     */
    public String getUserPropertiesQueryString() {
        return _userPropertiesQueryString;
    }

    public void setUserQueryString(String userQueryString) {
        _userQueryString = userQueryString;
    }

    public void setRolesQueryString(String rolesQueryString) {
        _rolesQueryString = rolesQueryString;
    }

    public void setCredentialsQueryString(String credentialsQueryString) {
        _credentialsQueryString = credentialsQueryString;
    }

    public void setUserPropertiesQueryString(String userPropertiesQueryString) {
        _userPropertiesQueryString = userPropertiesQueryString;
    }

    public String getResetCredentialDml () {
        return _resetCredentialDml;
    }

    public void setResetCredentialDml ( String resetCredentialDml ) {
        this._resetCredentialDml = resetCredentialDml;
    }

    public String getRelayCredentialQueryString () {
        return _relayCredentialQueryString;
    }

    public void setRelayCredentialQueryString ( String relayCredentialQueryString ) {
        this._relayCredentialQueryString = relayCredentialQueryString;
    }

    public boolean isUseColumnsAsPropertyNames() {
        return _useColumnsAsPropertyNames;
    }

    public void setUseColumnsAsPropertyNames(boolean useColumnsAsPropertyNames) {
        this._useColumnsAsPropertyNames = useColumnsAsPropertyNames;
    }
}
