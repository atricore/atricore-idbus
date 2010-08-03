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

import org.atricore.idbus.kernel.main.authn.SSORole;
import org.atricore.idbus.kernel.main.authn.SSOUser;
import org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;

/**
 * This is the service interface exposed to JOSSO Agents and external JOSSO Identity Service consumers.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Rev: 1359 $ $Date: 2009-07-19 13:57:57 -0300 (Sun, 19 Jul 2009) $
 */
public interface SSOIdentityManagerService {

    /**
     * Finds a user based on its name.  The name is a unique identifier of the user in the security domain, probably the user login.
     *
     * @param securityDomain, the security domain associated to the user
     * @param name            the user login name.
     * @throws org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException
     *          if the user does not exist.
     */
    SSOUser findUser(String securityDomain, String name)
            throws NoSuchUserException, SSOIdentityException;

    /**
     * Finds the user associated to a sso session
     *
     * @param sessionId the sso session identifier
     * @throws org.atricore.idbus.kernel.main.store.exceptions.NoSuchUserException
     *          if no user is associated to this session id.
     */
    SSOUser findUserInSession(String sessionId)
            throws NoSuchUserException, SSOIdentityException;

    /**
     * Finds an array of user's roles.
     * Elements in the collection are SSORole instances.
     *
     * @param ssoSessionId
     * @throws org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException
     *
     */
    SSORole[] findRolesBySSOSessionId(String ssoSessionId)
            throws SSOIdentityException;

    /**
     * This method validates that the received username matchs an existing user
     *
     * @param username
     * @throws NoSuchUserException  if the user does not exists or is invalid.
     * @throws SSOIdentityException if an error occurs while checking if user exists.
     */
    void userExists(String securityDomain, String username) throws NoSuchUserException, SSOIdentityException;
}
