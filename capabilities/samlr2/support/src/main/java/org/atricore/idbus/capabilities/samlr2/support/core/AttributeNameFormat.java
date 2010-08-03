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

package org.atricore.idbus.capabilities.samlr2.support.core;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: AttributeNameFormat.java 1208 2009-05-22 20:27:42Z sgonzalez $
 */
public enum AttributeNameFormat {
    
    /**
     * SAML 2.0 Core, Attribute Name Format Unspecified (section 8.2.1)
     */
    UNSPECIFIED("urn:oasis:names:tc:SAML:2.0:attrname-format:unspecified"),

    /**
     * SAML 2.0 Core, Attribute Name Format URI (section 8.2.2)
     */
    URI("urn:oasis:names:tc:SAML:2.0:attrname-format:uri"),

    /**
     * SAML 2.0 Core, Attribute Name Format Basic (section 8.2.3)
     */
    BASIC("urn:oasis:names:tc:SAML:2.0:attrname-format:basic");

    private String fmt;

    AttributeNameFormat(String fmt) {
        this.fmt = fmt;
    }

    public String getValue() {
        return fmt;
    }

    public AttributeNameFormat fromString(String f) {
        for (AttributeNameFormat fmt : values()) {
            if (fmt.getValue().equals(f))
                return fmt;
        }

        throw new IllegalArgumentException("Invalid Attribute Name format '"+f+"'");
    }

    @Override
    public String toString() {
        return fmt;
    }
}
