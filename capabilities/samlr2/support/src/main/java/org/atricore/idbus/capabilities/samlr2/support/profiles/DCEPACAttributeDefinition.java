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

package org.atricore.idbus.capabilities.samlr2.support.profiles;

/**
 * <h3>SAML 2.0 Profiles, 8.4.6 Attribute Definitions</h3>
 * <p>
 * The following are the set of SAML attributes defined by this profile. In each case, an xsi:type XML
 * attribute MAY be included in the &lt;AttributeValue&gt; element, but MUST have the value
 * dce:DCEValueType, where the dce prefix is arbitrary and MUST be bound to the XML namespace
 * urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE.
 * Note that such use of xsi:type will require validating attribute consumers to include the extension
 * schema defined by this profile.
 * </p>
 * 
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: DCEPACAttributeDefinition.java 1232 2009-06-01 22:43:42Z sgonzalez $
 */
public enum DCEPACAttributeDefinition {
    
    /**
     * <h3>SAML 2.0 Profiles, Attribute Definition Realm (section 8.4.6.1)</h3>
     * <p>
     * This single-valued attribute represents the SAML assertion subject's DCE realm or cell.
     * <strong>Name: urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:realm</strong>
     * The single &lt;AttributeValue&gt; element contains a UUID in URN form identifying the SAML assertion
     * subject's DCE realm/cell, with an optional profile-specific FriendlyName XML attribute containing the
     * realm's string name.
     * </p>
     */
    REALM("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:realm"),

    /**
     * <h3>SAML 2.0 Profiles, Attribute Definition Principal (section 8.4.6.2)</h3>
     * <p>
     * This single-valued attribute represents the SAML assertion subject's DCE principal identity.
     * <strong>Name: urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:principal</strong>
     * The single &lt;AttributeValue&gt; element contains a UUID in URN form identifying the SAML assertion
     * subject's DCE principal identity, with an optional profile-specific FriendlyName XML attribute containing
     * the principal's string name.
     * The profile-specific Realm XML attribute MAY be included and MUST contain a UUID in URN form
     * identifying the SAML assertion subject's DCE realm/cell (the value of the attribute defined in Section
     * 8.4.6.1).
     * </p>
     */
    PRINCIPAL("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:principal"),

    /**
     * <h3>SAML 2.0 Profiles, Attribute Definition Primary Group (section 8.4.6.3)</h3>
     * <p>
     * This single-valued attribute represents the SAML assertion subject's primary DCE group membership.
     * <strong>Name: urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:primary-group</strong>
     * The single &lt;AttributeValue&gt; element contains a UUID in URN form identifying the SAML assertion
     * subject's primary DCE group, with an optional profile-specific FriendlyName XML attribute containing
     * the group's string name.
     * The profile-specific Realm XML attribute MAY be included and MUST contain a UUID in URN form
     * identifying the SAML assertion subject's DCE realm/cell (the value of the attribute defined in Section
     * 8.4.6.1).
     * </p>
     */
    GROUP("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:primary-group"),

    /**
     * <h3>SAML 2.0 Profiles, Attribute Definition Groups (section 8.4.6.4 )</h3>
     * <p>
     * This multi-valued attribute represents the SAML assertion subject's DCE local group memberships.
     * <strong>Name: urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups</strong>
     * Each &lt;AttributeValue&gt; element contains a UUID in URN form identifying a DCE group membership
     * of the SAML assertion subject, with an optional profile-specific FriendlyName XML attribute containing
     * the group's string name.
     * The profile-specific Realm XML attribute MAY be included and MUST contain a UUID in URN form
     * identifying the SAML assertion subject's DCE realm/cell (the value of the attribute defined in Section
     * 8.4.6.1).
     * </p>
     */
    GROUPS("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups"),

    /**
     * <h3>SAML 2.0 Profiles, Attribute Definition 8.4.6.5 Foreign Groups</h3>
     * <p>
     * This multi-valued attribute represents the SAML assertion subject's DCE foreign group memberships.
     * <strong>Name: urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:foreign-groups</strong>
     * Each &lt;AttributeValue&gt; element contains a UUID in URN form identifying a DCE foreign group
     * membership of the SAML assertion subject, with an optional profile-specific FriendlyName XML attribute
     * containing the group's string name.
     * The profile-specific Realm XML attribute MUST be included and MUST contain a UUID in URN form
     * identifying the DCE realm/cell of the foreign group.
     * </p>
     */
    FOREIGN_GROUPS("urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:foreign-groups");

    private String attr;

    DCEPACAttributeDefinition(String attr) {
        this.attr = attr;
    }

    public DCEPACAttributeDefinition asEnum(String a) {
        for (DCEPACAttributeDefinition attr : values()) {
            if (attr.getValue().equals(a))
                return attr;
        }
        throw new IllegalArgumentException("Invalid Attribute Definition '"+ a + "'");
    }

    public String getValue() {
        return attr;
    }

    @Override
    public String toString() {
        return attr;
    }
}
