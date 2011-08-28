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

package org.atricore.idbus.capabilities.sso.support.profiles;

/**
 * ----------------------------------------------------
 * 4. SSO Profiles
 * ----------------------------------------------------
 * 4.1 Web browser SSO
 * urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser
 * 
 * 4.3 Identity Provider Discovery Profile
 * 
 * 4.2 Enhanced Client or Proxy (ECP) Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp
 * 
 * 4.4 Single Logout Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:SSO:logout
 *                                                 
 * 4.5 Name Identifier Management Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:SSO:nameid-mgmt
 * 
 * ----------------------------------------------------
 * 5. Artifact Resolution
 * ----------------------------------------------------
 * urn:oasis:names:tc:SAML:2.0:profiles:artifact
 * 
 * ----------------------------------------------------
 * 6. Assertion Query/Request Profile
 * ----------------------------------------------------
 * urn:oasis:names:tc:SAML:2.0:profiles:query
 * 
 * ----------------------------------------------------
 * 7. Name Identifier Mapping Profile
 * ----------------------------------------------------
 * urn:oasis:names:tc:SAML:2.0:profiles:nameidmapping
 * 
 * ----------------------------------------------------
 * 8. SAML Attribute Profiles
 * ----------------------------------------------------
 * 
 * 8.1 Basic Attribute Profile
 * 
 * 8.2 X.500/LDAP Attribute Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:attribute:X500
 * 
 * 8.3 UUID Attribute Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:attribute:UUID
 * 
 * 8.4 DCE PAC Attribute Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE
 * 
 * 8.5 8.5 XACML Attribute Profile
 * urn:oasis:names:tc:SAML:2.0:profiles:attribute:XACML
 *
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: Profile.java 1232 2009-06-01 22:43:42Z sgonzalez $
 */
public enum Profile {

    SSO_LOGOUT("urn:oasis:names:tc:SAML:2.0:profiles:SSO:logout"),

    SSO_BROWSER("urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser"),

    SSO_ECP("urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp"),

    SSO_NAMEID_MGMT("urn:oasis:names:tc:SAML:2.0:profiles:SSO:nameid-mgmt");


    private String profile;

    Profile(String profile) {
        this.profile = profile;
    }

    public Profile asEnum(String p) {
        for (Profile profile : Profile.values()) {
            if (profile.getValue().equals(p))
                return profile;
        }

        throw new IllegalArgumentException("Invalid SAMLR2 Profile name " + p);

    }

    public String getValue() {
        return profile;
    }


    @Override
    public String toString() {
        return profile;
    }
}
