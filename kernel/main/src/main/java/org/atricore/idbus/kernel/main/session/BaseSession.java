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

package org.atricore.idbus.kernel.main.session;

import org.atricore.idbus.kernel.main.authn.SecurityToken;


/**
 * A base implementation of an SSO session, that provides extra functionality used by the service.
 * This is a mutable SSO session.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BaseSession.java 1123 2009-04-06 04:49:27Z gbrigand $
 */

public interface BaseSession extends SSOSession {

    /**
     * Update the accessed time information for this session.
     */
    void access();

    /**
     * This method expires a session.  The isValid method will return false.
     */
    void expire();

    /**
     * Set the id of this session, used when initializing new sessions.
     * Used while building or recycling a session.
     *
     * @param id the session id.
     */
    void setId(String id);

    /**
     * Set the creation time for this session.
     * Used while building or recycling a session.
     *
     * @param time The new creation time
     */
    void setCreationTime(long time);

    /**
     * Set the valid flag for this session.
     * Used while building or recycling a session.
     *
     * @param valid The new value for the valid property.
     */
    void setValid(boolean valid);


    /**
     * Setter for the username associated to this session.
     */
    void setUsername(String name);

    /**
     * Setter for the security token associated with this session
     */
    void setSecurityToken(SecurityToken securityToken);

    void setLastNode(String node);

    String getLastNode();

}
