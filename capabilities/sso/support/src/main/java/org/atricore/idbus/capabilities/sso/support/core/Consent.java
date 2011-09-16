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

package org.atricore.idbus.capabilities.sso.support.core;

/**
 *
 * SAML 2.0 Core : 8.4 Consent Identifiers
 * The following identifiers MAY be used in the Consent attribute defined on the RequestAbstractType and
 * StatusResponseType complex types to communicate whether a principal gave consent, and under what
 * conditions, for the message.
 */
public enum Consent {


    /**
     * 8.4.1 Unspecified
     * No claim as to principal consent is being made.
     */

    Unspecified("urn:oasis:names:tc:SAML:2.0:consent:unspecified"),

    /**
     * 8.4.2 Obtained
     * Indicates that a principal’s consent has been obtained by the issuer of the message.
     */
    Obtained("urn:oasis:names:tc:SAML:2.0:consent:obtained"),

    /**
     * 8.4.3 Prior
     * Indicates that a principal’s consent has been obtained by the issuer of the message at some point prior to
     * the action that initiated the message.
     */

    Prior("urn:oasis:names:tc:SAML:2.0:consent:prior"),

    /**
     * 8.4.4 Implicit
     * Indicates that a principal’s consent has been implicitly obtained by the issuer of the message during the
     * action that initiated the message, as part of a broader indication of consent. Implicit consent is typically
     * more proximal to the action in time and presentation than prior consent, such as part of a session of
     * activities.
     */
    CurrentImplicit("urn:oasis:names:tc:SAML:2.0:consent:current-implicit"),

    /**
     * 8.4.5 Explicit
     * Indicates that a principal’s consent has been explicitly obtained by the issuer of the message during the
     * action that initiated the message.
     */
    CurrentExplicit("urn:oasis:names:tc:SAML:2.0:consent:current-explicit"),

    /**
     * 8.4.6 Unavailable
     * Indicates that the issuer of the message did not obtain consent.
     */
    Unavailable("urn:oasis:names:tc:SAML:2.0:consent:unavailable"),

    /**
     * 8.4.7 Inapplicable
     * Indicates that the issuer of the message does not believe that they need to obtain or report consent.
     */
    Inapplicable("urn:oasis:names:tc:SAML:2.0:consent:inapplicable");

    private String value;

    Consent(String value) {
        this.value = value;
    }

    public static Consent asEnum(String v) {
        for (Consent c: values()) {
            if (c.getValue().equals(v))
                return c;
        }
        throw new IllegalArgumentException("Invalid Consent value " + v);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
