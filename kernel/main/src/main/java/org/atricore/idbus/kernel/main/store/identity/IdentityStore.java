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

package org.atricore.idbus.kernel.main.store.identity;

import org.atricore.idbus.kernel.main.authn.BaseRole;
import org.atricore.idbus.kernel.main.authn.BaseUser;
import org.atricore.idbus.kernel.main.store.UserKey;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;

/**
 * Represents a resource to store user information.
 * Implementations define the specific persistence mechanism to store data.
 * User data is accessed using the username as key.
 *
 * TODO : Add component lifecycle : start, stop
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: IdentityStore.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public interface IdentityStore {

    /**
     * Loads user information from the store.
     *
     * @param key the user name, this is a unique identifier.
     * @return the found user
     * @throws NoSuchUserException if the user does not exist.
     */
    BaseUser loadUser(UserKey key) throws NoSuchUserException, SSOIdentityException;

    /**
     * Finds the list of roles associated to a given username.
     *
     * @param key
     * @return a list with BaseRole instances.
     * @throws SSOIdentityException
     */
    BaseRole[] findRolesByUserKey(UserKey key)
            throws SSOIdentityException;

    /**
     * Checks if the user is registered in the store.
     *
     * @param key
     * @throws SSOIdentityException
     */
    boolean userExists(UserKey key) throws SSOIdentityException;

    boolean isUpdatePasswordEnabled();

    void updatePassword(UserKey key, String currentPassword, String newPassword) throws SSOIdentityException;
}
