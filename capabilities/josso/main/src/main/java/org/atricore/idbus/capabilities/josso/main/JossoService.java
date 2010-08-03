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

package org.atricore.idbus.capabilities.josso.main;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public enum JossoService {

    SingleSignOnService(new QName(JossoConstants.JOSSO_SERVICE_BASE_URI, "SingleSignOnService")),

    SingleLogoutService(new QName(JossoConstants.JOSSO_SERVICE_BASE_URI, "SingleLogoutService")),

    AssertionConsumerService(new QName(JossoConstants.JOSSO_SERVICE_BASE_URI, "AssertionConsumerService")),

    IdentityManager(new QName(JossoConstants.JOSSO_SERVICE_BASE_URI, "SSOIdentityManager")),

    SessionManager(new QName(JossoConstants.JOSSO_SERVICE_BASE_URI, "SSOSessionManager")),

    IdentityProvider(new QName(JossoConstants.JOSSO_SERVICE_BASE_URI, "SSOIdentityProvider"));

    private QName qname;

    JossoService(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    JossoService(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }

    public static JossoService asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";
        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static JossoService asEnum(QName qname) {
        for (JossoService et : values()) {
            if (et.getQname().equals(qname))
                return et;
        }

        throw new IllegalArgumentException("Invalid endpoint type: " + qname);
    }

    @Override
    public String toString() {
        return qname.toString();
    }
}
