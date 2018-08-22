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
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * JDBC Identity DAO, used by AbstractDBIdentityStore.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IdentityDAO.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public class IdentityDAO {

    private static final Log logger = LogFactory.getLog(IdentityDAO.class);

    private Connection _conn;
    private CredentialProvider _cp;

    private String _userQueryString;
    private int _userQueryVariables;

    private String _rolesQueryString;
    private int _rolesQueryVariables = 0;

    private String _credentialsQueryString;
    private int _credentialsQueryVariables;

    private String _userPropertiesQueryString;
    private int _userPropertiesQueryVariables = 0; // This will be calcultated when setting _userPropertiesQueryString

    private String _resetCredentialDml;
    private String _relayCredentialQueryString;

    private boolean _useColumnNamesAsPropNames = false;

    public IdentityDAO(Connection conn,
                       CredentialProvider cp,
                       String userQueryString,
                       String rolesQueryString,
                       String credentialsQueryString,
                       String userPropertiesQueryString,
                       String resetCredentialDml,
                       String relayCredentialQueryString,
                       boolean useColumnNamesAsPropNames) {

        _conn = conn;
        _cp = cp;

        _userQueryString = userQueryString;
        _userQueryVariables = countQueryVariables(userQueryString);

        _rolesQueryString = rolesQueryString;
        _rolesQueryVariables = countQueryVariables(rolesQueryString);

        _credentialsQueryString = credentialsQueryString;
        _credentialsQueryVariables = countQueryVariables(credentialsQueryString);

        _resetCredentialDml = resetCredentialDml;
        _relayCredentialQueryString = relayCredentialQueryString;

        // User properties query :
        if (userPropertiesQueryString != null) {
            _userPropertiesQueryString = userPropertiesQueryString;
            _userPropertiesQueryVariables = countQueryVariables(_userPropertiesQueryString);
        }
        _useColumnNamesAsPropNames = useColumnNamesAsPropNames;
    }


    public IdentityDAO(Connection conn,
                       CredentialProvider cp,
                       String userQueryString,
                       String rolesQueryString,
                       String credentialsQueryString,
                       String userPropertiesQueryString,
                       String resetCredentialDml,
                       String relayCredentialQueryString) {
        this(conn,
                cp,
                userQueryString,
                rolesQueryString,
                credentialsQueryString,
                userPropertiesQueryString,
                resetCredentialDml,
                relayCredentialQueryString,
                false);

    }

    public BaseUser selectUser(SimpleUserKey key) throws SSOIdentityException {
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {

            stmt = createPreparedStatement(_userQueryString);
            // We don't jave JDBC 3.0 drivers, so ... bind all variables manually
            for (int i = 1; i <= _userQueryVariables; i++) {
                stmt.setString(i, key.getId());
            }
            result = stmt.executeQuery();

            BaseUser user = fetchUser(result);
            if (user == null)
                throw new SSOIdentityException("Can't find user for : " + key);

            return user;
        } catch (SQLException sqlE) {
            logger.error("SQLException while listing user", sqlE);
            throw new SSOIdentityException("During user listing: " + sqlE.getMessage());
        } catch (IOException ioE) {
            logger.error("IOException while listing user", ioE);
            throw new SSOIdentityException("During user listing: " + ioE.getMessage());
        } catch (Exception e) {
            logger.error("Exception while listing user", e);
            throw new SSOIdentityException("During user listing: " + e.getMessage());
        } finally {
            closeResultSet(result);
            closeStatement(stmt);
        }

    }

    public BaseRole[] selectRolesByUserKey(SimpleUserKey key) throws SSOIdentityException {
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {

            stmt = createPreparedStatement(_rolesQueryString);
            // We don't jave JDBC 3.0 drivers, so ... bind all variables manually
            for (int i = 1; i <= _rolesQueryVariables; i++) {
                stmt.setString(i, key.getId());
            }
            result = stmt.executeQuery();

            BaseRole[] roles = fetchRoles(result);

            return roles;
        } catch (SQLException sqlE) {
            logger.error("SQLException while listing roles", sqlE);
            throw new SSOIdentityException("During roles listing: " + sqlE.getMessage());

        } catch (IOException ioE) {
            logger.error("IOException while listing roles", ioE);
            throw new SSOIdentityException("During roles listing: " + ioE.getMessage());

        } catch (Exception e) {
            logger.error("Exception while listing roles", e);
            throw new SSOIdentityException("During roles listing: " + e.getMessage());

        } finally {
            closeResultSet(result);
            closeStatement(stmt);
        }
    }

    public Credential[] selectCredentials(SimpleUserKey key) throws SSOIdentityException {
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {

            if (logger.isDebugEnabled())
                logger.debug("[selectCredemtiasl()]]: key=" + key.getId());
            stmt = createPreparedStatement(_credentialsQueryString);
            // We don't jave JDBC 3.0 drivers, so ... bind all variables manually
            for (int i = 1; i <= _credentialsQueryVariables; i++) {
                stmt.setString(i, key.getId());
            }
            result = stmt.executeQuery();

            Credential[] creds = fetchCredentials(result);

            return creds;
        } catch (SQLException sqlE) {
            logger.error("SQLException while listing credentials", sqlE);
            throw new SSOIdentityException("During credentials listing: " + sqlE.getMessage());

        } catch (IOException ioE) {
            logger.error("IOException while listing credentials", ioE);
            throw new SSOIdentityException("During credentials listing: " + ioE.getMessage());

        } catch (Exception e) {
            logger.error("Exception while listing credentials", e);
            throw new SSOIdentityException("During credentials listing: " + e.getMessage());

        } finally {
            closeResultSet(result);
            closeStatement(stmt);
        }

    }

    /**
     * This will execute the configured query to get all user properties.
     * Because we sugested the use of 'UNION' key words to retrieve properties from multiple columns/tables,
     * we probably must send multiple times the username value to "avoid not all variables bound" error.
     * We could avoid this by using JDBC 3.0 drivers in the future.
     *
     * @param key
     * @throws SSOIdentityException
     */

    public SSONameValuePair[] selectUserProperties(SimpleUserKey key) throws SSOIdentityException {
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {

            if (logger.isDebugEnabled())
                logger.debug("[selectUserProperties()]]: key=" + key.getId());
            stmt = createPreparedStatement(_userPropertiesQueryString);

            // We don't jave JDBC 3.0 drivers, so ... bind all variables manually
            for (int i = 1; i <= _userPropertiesQueryVariables; i++) {
                stmt.setString(i, key.getId());
            }

            result = stmt.executeQuery();

            SSONameValuePair[] props = _useColumnNamesAsPropNames ?
                    fecthSSONameValuePairsFromCols(result) :
                    fetchSSONameValuePairsFromRows(result);

            if (logger.isTraceEnabled())
                logger.trace("Retrieved " + props.length + " user properties");

            return props;
        } catch (SQLException sqlE) {
            logger.error("SQLException while listing user properties", sqlE);
            throw new SSOIdentityException("During user properties listing: " + sqlE.getMessage());

        } catch (IOException ioE) {
            logger.error("IOException while listing user properties", ioE);
            throw new SSOIdentityException("During user properties listing: " + ioE.getMessage());

        } catch (Exception e) {
            logger.error("Exception while listing user properties", e);
            throw new SSOIdentityException("During user properties listing: " + e.getMessage());

        } finally {
            closeResultSet(result);
            closeStatement(stmt);
        }
    }

    public void resetCredential ( SimpleUserKey key, BaseCredential newPassword) throws SSOIdentityException {
        PreparedStatement stmt = null;
        try {
            if (logger.isDebugEnabled())
                logger.debug("[resetCredential()]]: key=" + key.getId());

            stmt = createPreparedStatement( _resetCredentialDml );
            stmt.setString( 1, newPassword.getValue().toString() );
            stmt.setString( 2, key.getId() );
            stmt.execute();
            _conn.commit();

        } catch (SQLException e) {
            logger.error("SQLException while updating user credential", e);
            throw new SSOIdentityException("During user update credential: " + e.getMessage());
        } finally {
            closeStatement( stmt );
        }
    }

    public String resolveUsernameByRelayCredential(String name, String value) throws SSOIdentityException {
        PreparedStatement stmt = null;
        ResultSet result = null;

        try {
            if (logger.isDebugEnabled())
                logger.debug("[resolveUsernameByRelayCredential(name, value)]]: name=" + name + " value=" + value);

            if( _relayCredentialQueryString.contains( "#?#" )){
                stmt = createPreparedStatement( _relayCredentialQueryString.replace( "#?#", name ));
                stmt.setString( 1, value );
            } else {
                stmt = createPreparedStatement( _relayCredentialQueryString);
                stmt.setString( 1, name );
                stmt.setString( 2, value );
            }
            result = stmt.executeQuery();

            String username = result.next() ? result.getString( 1 ) : null;
            if( result.next() ){
                throw new SSOIdentityException( "Statement " + stmt + " returned more than one row" );
            }
            return username; 

        } catch (SQLException sqlE) {
            logger.error("SQLException while loading user with relay credential", sqlE);
            throw new SSOIdentityException("During load user with relay credential: " + sqlE.getMessage());

        } catch (Exception e) {
            logger.error("Exception while loading user with relay credential", e);
            throw new SSOIdentityException("During load user with relay credential: " + e.getMessage());

        } finally {
            closeResultSet(result);
            closeStatement(stmt);
        }
    }

    // ------------------------------------------------------------------------------------------
    // Protected DB utils.
    // ------------------------------------------------------------------------------------------

    /**
     * Builds an array of credentials based on a ResultSet
     * Column names are used to build a credential.
     */
    protected Credential[] fetchCredentials(ResultSet rs)
            throws SQLException, IOException, SSOAuthenticationException {

        if (rs.next()) {

            List creds = new ArrayList();
            ResultSetMetaData md = rs.getMetaData();

            // Each column is a credential, the column name is used as credential name ...
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String cName = md.getColumnLabel(i);
                String cValue = rs.getString(i);
                Credential c = _cp.newCredential(cName, cValue);
                creds.add(c);
            }

            return (Credential[]) creds.toArray(new Credential[creds.size()]);
        }

        return new Credential[0];

    }

    protected SSONameValuePair[] fecthSSONameValuePairsFromCols(ResultSet rs) throws SQLException {
        List<SSONameValuePair> props = new ArrayList<SSONameValuePair>();

        ResultSetMetaData rsmd = rs.getMetaData();

        int cols = rsmd.getColumnCount();

        while (rs.next()) {
            for (int i = 1; i <= cols; i++) {

                // SELECT CNAME AS CLABEL ...
                String cName = rsmd.getColumnName(i);
                String cLabel = rsmd.getColumnLabel(i);

                String cValue = rs.getString(i);
                SSONameValuePair prop = new SSONameValuePair(cLabel != null && !"".equals(cLabel) ? cLabel : cName, cValue);
                props.add(prop);
            }
        }

        return props.toArray(new SSONameValuePair[props.size()]);
    }

    /**
     * Builds an array of name-value pairs on a ResultSet
     * The resultset must have two columns, the first one contains names and the second one values.
     */
    protected SSONameValuePair[] fetchSSONameValuePairsFromRows(ResultSet rs)
            throws SQLException, IOException, SSOAuthenticationException {
        List<SSONameValuePair> props = new ArrayList<SSONameValuePair>();

        while (rs.next()) {
            // First column is a name and second is a value.
            String cName = rs.getString(1);
            String cValue = rs.getString(2);
            SSONameValuePair prop = new SSONameValuePair(cName, cValue);
            props.add(prop);
        }

        return props.toArray(new SSONameValuePair[props.size()]);
    }


    /**
     * Builds a user based on a result set.
     * ResultSet must have one and only one record.
     */
    protected BaseRole[] fetchRoles(ResultSet rs)
            throws SQLException, IOException {

        List roles = new ArrayList();

        while (rs.next()) {

            BaseRole role = new BaseRoleImpl();
            String rolename = rs.getString(1);
            role.setName(rolename);

            roles.add(role);

        }

        return (BaseRole[]) roles.toArray(new BaseRole[roles.size()]);
    }

    /**
     * Builds a user based on a result set.
     */
    protected BaseUser fetchUser(ResultSet rs)
            throws SQLException, IOException {

        if (rs.next()) {
            BaseUser user = new BaseUserImpl();
            String username = rs.getString(1);
            user.setName(username);

            return user;
        }

        return null;
    }


    /**
     * Creates a new prepared statement for the received query string.
     *
     * @param query
     * @throws SQLException
     */
    protected PreparedStatement createPreparedStatement(String query)
            throws SQLException {

        if (logger.isDebugEnabled())
            logger.debug("[createPreparedStatement()] : " + "(" + query + ")");

        PreparedStatement stmt =
                _conn.prepareStatement(query + " ");

        return stmt;
    }

    protected void closeStatement(PreparedStatement stmt)
            throws SSOIdentityException {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException se) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error clossing statement");
            }

            throw new SSOIdentityException("Error while clossing statement: \n " + se.getMessage());

        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error clossing statement");
            }

            // throw new SSOIdentityException("Error while clossing statement: \n " + e.getMessage());
        }
    }


    protected void closeResultSet(ResultSet result)
            throws SSOIdentityException {
        try {
            if (result != null) {
                result.close();
            }
        } catch (SQLException se) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error while clossing result set");
            }

            throw new SSOIdentityException("SQL Exception while closing\n" + se.getMessage());
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error while clossing result set");
            }

            // throw new SSOIdentityException("Exception while closing Result Set\n" + e.getMessage());

        }
    }

    /**
     * This util counts the number of times that the '?' char appears in the received query string.
     */
    protected int countQueryVariables(String qry) {
        StringTokenizer st = new StringTokenizer(qry, "?", true);
        int count = 0;
        while (st.hasMoreTokens()) {
            String tk = st.nextToken();
            if ("?".equals(tk)) {
                count++;
            }
        }
        return count;
    }


}
