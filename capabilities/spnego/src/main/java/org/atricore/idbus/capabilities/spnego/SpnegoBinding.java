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
public enum SpnegoBinding {

    /** URI for IDBUS HTTP Artifact binding, this is NOT SAML Normative*/
    SSO_ARTIFACT("urn:org:atricore:idbus:sso:bindings:HTTP-Artifact"),

    /** URI for SPNEGO over HTTP Binding */
    SPNEGO_HTTP_INITIATION("urn:org:atricore:idbus:spnego:bindings:HTTP-INITIATION"),

    /** URI for SPNEGO over HTTP Binding */
    SPNEGO_HTTP_NEGOTIATION("urn:org:atricore:idbus:spnego:bindings:HTTP-NEGOTIATION");

    private String binding;

    SpnegoBinding(String binding) {
        this.binding = binding;
    }

    public String getValue() {
        return binding;
    }

    @Override
    public String toString() {
        return binding;
    }

    public static SpnegoBinding asEnum(String binding) {
        for (SpnegoBinding b : values()) {
            if (b.getValue().equals(binding))
                return b;
        }

        throw new IllegalArgumentException("Invalid Spnego Binding '" + binding + "'");
    }
}
