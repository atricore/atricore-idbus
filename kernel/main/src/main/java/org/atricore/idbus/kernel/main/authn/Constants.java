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
 * Some constants used by front-channel http.
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: Constants.java 1168 2009-04-27 20:18:19Z ajadzinsky $
 */

public interface Constants  {

    public static final String PASSWORD_NS = "urn:org:atricore:idbus:kernel:main:authn:pwd";

    public static final String PASSCODE_NS = "urn:org:atricore:idbus:kernel:main:authn:passcode";

    public static final String SPNEGO_NS = "urn:org:atricore:idbus:kernel:main:authn:spnego";

    public static final String TICKET_NS = "urn:org:atricore:idbus:kernel:main:authn:ticket";

    public static final String REMEMBERME_NS = "urn:org:atricore:idbus:kernel:main:authn:remember-me";

    public static final String IMPERSONATE_NS = "urn:org:atricore:idbus:kernel:main:authn:impersonate";

    public static final String PROXY_NS = "urn:org:atricore:idbus:kernel:main:authn:proxy";

    public static final String PREVIOUS_SESSION_NS = "urn:org:atricore:idbus:kernel:main:authn:previous-session";

    public static final String CACHE_NS = "urn:org:atricore:idbus:kernel:main:authn:cache";

    public static final String AUTHZ_CODE_NS = "urn:org:atricore:idbus:kernel:main:authn:authz-code";

    public static final String AUTHN_SOURCE = "urn:org:atricore:idbus:kernel:main:sts:AuthnSource";

    public static final String PWDLESS_LINK = "urn:org:atricore:idbus:kernel:main:authn:pwdless-link";;
}
