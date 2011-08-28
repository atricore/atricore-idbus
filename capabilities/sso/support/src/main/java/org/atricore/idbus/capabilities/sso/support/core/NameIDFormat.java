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
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: NameIDFormat.java 1341 2009-06-25 18:41:51Z sgonzalez $
 */
public enum NameIDFormat {
    
    /**
     * SAML 2.0 Core, Name Identifier Format Unspecified (section 8.3.1)
     */
    UNSPECIFIED("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"),

    /**
     * SAML 2.0 Core, Name Identifier Format Email Address (section 8.3.2)
     */
    EMAIL("urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"),

    /**
     * SAML 2.0 Core, Name Identifier Format X.509 Subject Name (section 8.3.3)
     */
    X509_SUBJECT("urn:oasis:names:tc:SAML:1.1:nameid-format:x509SubjectName"),

    /**
     * SAML 2.0 Core, Name Identifier Format Windows Domain Qualified Name (section 8.3.4)
     */
    WIN_DOMAIN_QUALIFIED("urn:oasis:names:tc:SAML:1.1:nameid-format:WindowsDomainQualifiedName"),

    /**
     * SAML 2.0 Core, Name Identifier Format Kerberos Principal Name (section 8.3.5)
     */
    KERBEROS("urn:oasis:names:tc:SAML:2.0:nameid-format:kerberos"),

    /**
     * SAML 2.0 Core, Name Identifier Format Entity Identifier (section 8.3.6)
     */
    ENTITY("urn:oasis:names:tc:SAML:2.0:nameid-format:entity"),

    /**
     * SAML 2.0 Core, Name Identifier Format Persistent Identifier (section 8.3.7)
     */
    PERSISTENT("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"),

    /**
     * SAML 2.0 Core, Name Identifier Format Transient Identifier (section 8.3.8)
     */
    TRANSIENT("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"),

    /**
     * SAML 2.0 Core, Additional Identifer format for Element &lt),NameIDPolicy&gt), (section 3.4.1.1)
     */
    ENCRYPTED("urn:oasis:names:tc:SAML:2.0:nameid-format:encrypted");

    private String fmt;

    NameIDFormat(String fmt) {
        this.fmt = fmt;
    }

    public static NameIDFormat asEnum(String f) {
        for (NameIDFormat fmt : values()) {
            if (fmt.getValue().equals(f))
                return fmt;
        }

        throw new IllegalArgumentException("Invalid NameID format '"+f+"'");
    }

    public String getValue() {
        return fmt;
    }

    @Override
    public String toString() {
        return fmt;
    }
}
