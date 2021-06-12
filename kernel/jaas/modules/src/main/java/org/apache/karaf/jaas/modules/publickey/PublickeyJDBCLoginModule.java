/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.karaf.jaas.modules.publickey;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.Principal;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.osgi.framework.BundleContext;

import org.apache.karaf.jaas.modules.jdbc.JDBCUtils;
import org.apache.karaf.jaas.modules.AbstractKarafLoginModule;
import org.apache.karaf.jaas.modules.RolePrincipal;
import org.apache.karaf.jaas.modules.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.karaf.jaas.modules.encryption.BasicEncryption.base64Encode;

public class PublickeyJDBCLoginModule extends AbstractKarafLoginModule {

    private final Logger LOG = LoggerFactory.getLogger(PublickeyJDBCLoginModule.class);

    public static final String KEY_QUERY = "query.key";
    public static final String ROLE_QUERY = "query.role";

    private String datasourceURL;
    protected String keyQuery = "SELECT PUBLIC_KEY FROM USERS WHERE USERNAME=?";
    protected String roleQuery = "SELECT ROLE FROM ROLES WHERE USERNAME=?";

    private BundleContext bc;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        super.initialize(subject, callbackHandler, options);
        this.bc = (BundleContext) options.get(BundleContext.class.getName());
        datasourceURL = (String) options.get(JDBCUtils.DATASOURCE);
        keyQuery = (String) options.get(KEY_QUERY);
        roleQuery = (String) options.get(ROLE_QUERY);
        if (datasourceURL == null || datasourceURL.trim().length() == 0) {
            LOG.error("No datasource was specified ");
        } else if (!datasourceURL.startsWith(JDBCUtils.JNDI) && !datasourceURL.startsWith(JDBCUtils.OSGI)) {
            LOG.error("Invalid datasource lookup protocol");
        }
    }

    public boolean login() throws LoginException {
        Connection connection = null;

        PreparedStatement keyStatement = null;
        PreparedStatement roleStatement = null;

        ResultSet keyResultSet = null;
        ResultSet roleResultSet = null;

        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Username: ");
        callbacks[1] = new PublickeyCallback();
        try {
            callbackHandler.handle(callbacks);
        } catch (IOException ioe) {
            throw new LoginException(ioe.getMessage());
        } catch (UnsupportedCallbackException uce) {
            throw new FailedLoginException(uce.getMessage() + " not available to obtain information from user");
        }

        user = ((NameCallback) callbacks[0]).getName();
        PublicKey pubKey = ((PublickeyCallback) callbacks[1]).getPublicKey();
        if (pubKey == null) {
            throw new FailedLoginException("Unable to retrieve public key");
        }


        String key = getString(pubKey);
        principals = new HashSet<Principal>();

        try {
            Object credentialsDatasource = JDBCUtils.createDatasource(bc, datasourceURL);

            if (credentialsDatasource == null) {
                throw new LoginException("Cannot obtain data source:" + datasourceURL);
            } else if (credentialsDatasource instanceof DataSource) {
                connection = ((DataSource) credentialsDatasource).getConnection();
            } else if (credentialsDatasource instanceof XADataSource) {
                connection = ((XADataSource) credentialsDatasource).getXAConnection().getConnection();
            } else {
                throw new LoginException("Unknow dataSource type " + credentialsDatasource.getClass());
            }

            //Retrieve user credentials from database.
            keyStatement = connection.prepareStatement(keyQuery);
            keyStatement.setString(1, user);
            keyResultSet = keyStatement.executeQuery();

            if (!keyResultSet.next()) {
                throw new LoginException("User " + user + " does not exist");
            } else {
                String storedKey = keyResultSet.getString(1);

                   if (!checkPassword(key, storedKey)) {
                    throw new LoginException("Password for " + user + " does not match");
                }
                principals.add(new UserPrincipal(user));
            }

            //Retrieve user roles from database
            roleStatement = connection.prepareStatement(roleQuery);
            roleStatement.setString(1, user);
            roleResultSet = roleStatement.executeQuery();
            while (roleResultSet.next()) {
                String role = roleResultSet.getString(1);
                principals.add(new RolePrincipal(role));
            }
        } catch (Exception ex) {
            throw new LoginException("Error has occured while retrieving credentials from database:" + ex.getMessage());
        } finally {
            try {
                if (keyResultSet != null) {
                    keyResultSet.close();
                }
                if (keyStatement != null) {
                    keyStatement.close();
                }
                if (roleResultSet != null) {
                    roleResultSet.close();
                }
                if (roleStatement != null) {
                    roleStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                LOG.warn("Failed to clearly close connection to the database:", ex);
            }
        }
        return true;

    }

    private String getString(PublicKey key) throws FailedLoginException {
        try {
            if (key instanceof DSAPublicKey) {
                DSAPublicKey dsa = (DSAPublicKey) key;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                write(dos, "ssh-dss");
                write(dos, dsa.getParams().getP());
                write(dos, dsa.getParams().getQ());
                write(dos, dsa.getParams().getG());
                write(dos, dsa.getY());
                dos.close();
                return base64Encode(baos.toByteArray());
            } else if (key instanceof RSAKey) {
                RSAPublicKey rsa = (RSAPublicKey) key;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                write(dos, "ssh-rsa");
                write(dos, rsa.getPublicExponent());
                write(dos, rsa.getModulus());
                dos.close();
                return base64Encode(baos.toByteArray());
            } else {
                throw new FailedLoginException("Unsupported key type " + key.getClass().toString());
            }
        } catch (IOException e) {
            throw new FailedLoginException("Unable to check public key");
        }
    }

    private void write(DataOutputStream dos, BigInteger integer) throws IOException {
        byte[] data = integer.toByteArray();
        dos.writeInt(data.length);
        dos.write(data, 0, data.length);
    }

    private void write(DataOutputStream dos, String str) throws IOException {
        byte[] data = str.getBytes();
        dos.writeInt(data.length);
        dos.write(data);
    }

    public boolean abort() throws LoginException {
        clear();
        if (debug) {
            LOG.debug("abort");
        }
        return true;
    }

    public boolean logout() throws LoginException {
        subject.getPrincipals().removeAll(principals);
        principals.clear();
        if (debug) {
            LOG.debug("logout");
        }
        return true;
    }

}
