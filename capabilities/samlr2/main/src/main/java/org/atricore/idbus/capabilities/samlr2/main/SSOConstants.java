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

package org.atricore.idbus.capabilities.samlr2.main;

/**
 * Definition of variables used during plans execution.
 *
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2PlansConstants.java 1342 2009-06-26 16:21:27Z sgonzalez $
 */
public interface SSOConstants {

    /**
     * The name of the cookie that holds the JOSSO Remember me token value
     */
    public static final String SSO_REMEMBERME_TOKEN = "SSO_REMEMBERME";

    /**
     * Reqeuest parameter representing an SSO command.
     * Value : sso_cmd
     */
    public static final String PARAM_SSO_USERNAME = "sso_username";

    /**
     * Reqeuest parameter representing an SSO command.
     * Value : sso_cmd
     */
    public static final String PARAM_SSO_PASSWORD = "sso_password";


}