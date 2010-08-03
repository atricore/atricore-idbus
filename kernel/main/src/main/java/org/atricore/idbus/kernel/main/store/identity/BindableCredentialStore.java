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

import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;

/**
 * This represents a credential store that can test a bind to the persistence mechanism using provided credentials.
 * Used in combination with a BindUsernamePasswordAuthScheme allows JOSSO to delegate authentication process to the
 * underlaying persistence mechanism, for example a DB or LDAP server like MS AD.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: BindableCredentialStore.java 1040 2009-03-05 00:56:52Z gbrigand $
 */
public interface BindableCredentialStore extends CredentialStore {

    boolean bind(String username, String password) throws SSOAuthenticationException;

}
