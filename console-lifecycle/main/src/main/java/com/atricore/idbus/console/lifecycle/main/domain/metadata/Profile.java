/*
 * Atricore IDBus
 *
 *   Copyright 2009, Atricore Inc.
 *
 *   This is free software; you can redistribute it and/or modify it
 *   under the terms of the GNU Lesser General Public License as
 *   published by the Free Software Foundation; either version 2.1 of
 *   the License, or (at your option) any later version.
 *
 *   This software is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this software; if not, write to the Free
 *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package com.atricore.idbus.console.lifecycle.main.domain.metadata;

public enum Profile {

    // ----------------------------------------------------
    // 4. SSO Profiles
    // ----------------------------------------------------
    // 4.1 Web browser SSO
    SSO("urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser"),

    // 4.3 Identity Provider Discovery Profile

    // 4.2 Enhanced Client or Proxy (ECP) Profile
    SSO_ECP("urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp"),

    // 4.4 Single Logout Profile
    SSO_SLO("urn:oasis:names:tc:SAML:2.0:profiles:SSO:logout"),

    // 4.5 Name Identifier Management Profile
    SSO_NAMEID("urn:oasis:names:tc:SAML:2.0:profiles:SSO:nameid-mgmt"),


    // ----------------------------------------------------
    // 5. Artifact Resolution
    // ----------------------------------------------------
    ARTIFACT("urn:oasis:names:tc:SAML:2.0:profiles:artifact"),

    // ----------------------------------------------------
    // 6. Assertion Query/Request Profile
    // ----------------------------------------------------
    ASSERTION_QUERY("urn:oasis:names:tc:SAML:2.0:profiles:query"),

    // ----------------------------------------------------
    // 7. Name Identifier Mapping Profile
    // ----------------------------------------------------
    NAMEID("urn:oasis:names:tc:SAML:2.0:profiles:nameidmapping"),

    // ----------------------------------------------------
    // 8. SAML Attribute Profiles
    // ----------------------------------------------------

    // 8.1 Basic Attribute Profile
    ATTR_BASIC("urn:oasis:names:tc:SAML:2.0:profiles:attribute:basic"), // TODO !

    // 8.2 X.500/LDAP Attribute Profile
    ATTR_X500("urn:oasis:names:tc:SAML:2.0:profiles:attribute:X500"),

    // 8.3 UUID Attribute Profile
    ATTR_UUID("urn:oasis:names:tc:SAML:2.0:profiles:attribute:UUID"),

    // 8.4 DCE PAC Attribute Profile
    ATTR_DCE("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE"),

    // 8.5 8.5 XACML Attribute Profile
    ATTR_XACML("urn:oasis:names:tc:SAML:2.0:profiles:attribute:XACML"),

    ;

    private String name;

    Profile(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
