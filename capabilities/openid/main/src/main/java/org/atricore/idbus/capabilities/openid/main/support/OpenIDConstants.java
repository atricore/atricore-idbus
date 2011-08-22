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

package org.atricore.idbus.capabilities.openid.main.support;

import javax.xml.namespace.QName;

/**
 *
 * TODO: GB/Refactor - Generic Constants need to live in the SSO module
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SAMLR2Constants.java 1287 2009-06-16 19:19:31Z sgonzalez $
 */
public interface OpenIDConstants {

    static final String OPENID10_VERSION = "http://openid.net/signon/1.0";
    static final String OPENID11_VERSION = "http://openid.net/signon/1.1";
    static final String OPENID2_VERSION = "http://specs.openid.net/auth/2.0/signon";

    static final QName SPInitiatedSingleSignOnService_QNAME = new QName("urn:org:atricore:idbus:sso:metadata", "SPInitiatedSingleSignOnService");

    static final String SSOUSER_PROPERTY_NS= "urn:org:atricore:idbus:user:property";

    static final String SSO_COMMON_PKG = "org.atricore.idbus.common.sso._1_0.protocol";

    static final String XHTML_PKG = "org.w3._1999.xhtml";

}
