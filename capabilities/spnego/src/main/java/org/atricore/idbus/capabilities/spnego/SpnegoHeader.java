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

package org.atricore.idbus.capabilities.spnego;

/**
 * @author <a href="mailto:gbrigandi@atricore.org">Gianluca Brigandi</a>
 * @version $Id$
 */
public enum SpnegoHeader {

    /**
     * HTTP Response Header <b>WWW-Authenticate</b>.
     */
     AUTHN("WWW-Authenticate"),

    /**
     * HTTP Request Header <b>Authorization</b>.
     */
    AUTHZ("Authorization"),

    /**
     * HTTP Response Header <b>Negotiate</b>.
     */
    NEGOTIATE("Negotiate");

    private String value;

    SpnegoHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static SpnegoHeader asEnum(String value) {
        for (SpnegoHeader b : values()) {
            if (b.getValue().equals(value))
                return b;
        }

        throw new IllegalArgumentException("Invalid Spnego Header '" + value + "'");
    }
}
