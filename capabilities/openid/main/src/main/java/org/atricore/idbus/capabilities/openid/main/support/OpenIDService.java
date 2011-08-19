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
 * SAML 2.0 Services realized by several roles.
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Service.java 1372 2009-07-23 15:03:28Z chromy96 $
 */
public enum OpenIDService {

    /**
     * This service is an extension to SAML
     */
    SPInitiatedSingleSignOnService(OpenIDConstants.SPInitiatedSingleSignOnService_QNAME);

    private QName qname;


    OpenIDService(String uri, String localPart) {
        this(new QName(uri, localPart));
    }

    OpenIDService(QName qname) {
        this.qname = qname;
    }

    public QName getQname() {
        return qname;
    }


    public static OpenIDService asEnum(String name) {
        String localPart = name.substring(name.lastIndexOf("}") + 1);
        String uri = name.lastIndexOf("}") > 0 ? name.substring(1, name.lastIndexOf("}")) : "";

        QName qname = new QName(uri,  localPart);
        return asEnum(qname);
    }

    public static OpenIDService asEnum(QName qname) {
        for (OpenIDService et : values()) {
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

