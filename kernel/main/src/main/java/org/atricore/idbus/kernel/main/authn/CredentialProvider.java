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

package org.atricore.idbus.kernel.main.authn;

import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * Interface to be implemented by components which can build user Credentials.
 *
 * @author <a href="mailto:gbrigand@josso.org">Gianluca Brigandi</a>
 * @version $Id: CredentialProvider.java 1040 2009-03-05 00:56:52Z gbrigand $
 */

public interface CredentialProvider {

    /**
     * Creates a new credential based on its name and value.
     *
     * @param name  the credential name
     * @param value the credential value
     * @return the Credential instance representing the supplied name-value pair.
     */
    Credential newCredential(String name, Object value);

    /**
     * Creates a new credential object based on its name and value. The value should be encoded if the credential allows it (i.e. password hash)
     * @param name
     * @param value
     * @return
     */
    Credential newEncodedCredential(String name, Object value);

    /**
     * Creates credentials for a User object
     *
     * @param user
     * @return
     */
    Credential[] newCredentials(User user);

}
