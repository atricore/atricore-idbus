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

package org.atricore.idbus.kernel.main.authn.scheme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @org.apache.xbean.XBean element="basic-auth-credential-provider" 
 */
public class UsernamePasswordCredentialProvider implements CredentialProvider {

    /**
     * The name of the credential representing a password.
     * Used to get a new credential instance based on its name and value.
     * Value : password
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String PASSWORD_CREDENTIAL_NAME = "password";

    /**
     * The name of the credential representing a user identifier.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String USERID_CREDENTIAL_NAME = "userid";

    /**
     * The name of the credential representing a username.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String USERNAME_CREDENTIAL_NAME = "username";


    /**
     * The name of the credential representing a salt.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String SALT_CREDENTIAL_NAME = "salt";


    private static final Log logger = LogFactory.getLog(UsernamePasswordCredentialProvider.class);

    /**
     * Creates a new credential based on its name and value.
     *
     * @param name  the credential name
     * @param value the credential value
     * @return the Credential instance representing the supplied name-value pair.
     */
    @Override
    public Credential newCredential(String name, Object value) {
        if (name.equalsIgnoreCase(USERID_CREDENTIAL_NAME)) {
            return new UserIdCredential(value);
        }

        if (name.equalsIgnoreCase(USERNAME_CREDENTIAL_NAME)) {
            return new UserNameCredential(value);
        }

        if (name.equalsIgnoreCase(PASSWORD_CREDENTIAL_NAME)) {
            return new PasswordCredential(value);
        }

        if (name.equalsIgnoreCase(SALT_CREDENTIAL_NAME)) {
            return new SaltCredential(value);
        }

        // Don't know how to handle this name ...
        if (logger.isDebugEnabled())
            logger.debug("Unknown credential name : " + name);

        return null;
    }

    /**
     * Creates a new 'encoded credential'
     * @param name
     * @param value
     * @return
     */
    @Override
    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {
        List<Credential> creds = new ArrayList<Credential>();

        creds.add(newCredential(USERNAME_CREDENTIAL_NAME, user.getUserName()));
        creds.add(newCredential(USERID_CREDENTIAL_NAME, user.getUserName()));
        creds.add(newCredential(PASSWORD_CREDENTIAL_NAME, user.getUserPassword()));
        if (user.getSalt() != null && !"".equals(user.getSalt()))
        creds.add(newCredential(SALT_CREDENTIAL_NAME, user.getSalt()));

        return creds.toArray(new Credential[0]);
    }
}
