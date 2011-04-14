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

/**
 * Some constants used by frontchannel http.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: Constants.java 1168 2009-04-27 20:18:19Z ajadzinsky $
 */

public interface Constants  {

    /**
     * Reqeuest parameter representing an SSO command.
     * Value : sso_cmd
     */
    public static final String PASSWORD_NS = "urn:org:atricore:idbus:kernel:main:authn:pwd";

    public static final String PASSCODE_NS = "urn:org:atricore:idbus:kernel:main:authn:passcode";

    public static final String REMEMBERME_NS = "urn:org:atricore:idbus:kernel:main:authn:remember-me";

    public static final String TWOFACTOR_NS = "urn:org:atricore:idbus:kernel:main:authn:2factor";



}
