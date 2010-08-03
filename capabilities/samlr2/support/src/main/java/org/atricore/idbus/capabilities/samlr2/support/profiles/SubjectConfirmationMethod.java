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
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SubjectConfirmationMethod.java 1208 2009-05-22 20:27:42Z sgonzalez $
 */
public enum SubjectConfirmationMethod {
    
    /**
     * <h3>SAML 2.0 Profiles, Holder Of Key (section 3.1)</h3>
     * <p>
     * One or more &lt;ds:KeyInfo&gt; elements MUST be present within the &lt;SubjectConfirmationData&gt;
     * element. An xsi:type attribute MAY be present in the &lt;SubjectConfirmationData&gt; element and, if
     * present, MUST be set to saml:KeyInfoConfirmationDataType (the namespace prefix is arbitrary but
     * must reference the SAML assertion namespace).
     * As described in [XMLSig], each <ds:KeyInfo> element holds a key or information that enables an
     * application to obtain a key. The holder of a specified key is considered to be the subject of the assertion
     * by the asserting party.
     * </p>
     * <p>
     * Note that in accordance with [XMLSig], each <ds:KeyInfo> element MUST identify a single
     * cryptographic key. Multiple keys MAY be identified with separate <ds:KeyInfo> elements, such as when
     * different confirmation keys are needed for different relying parties.
     * </p>
     * <p>
     * Example: The holder of the key named "By-Tor" or the holder of the key named "Snow Dog" can confirm
     * itself as the subject.
     * </p>
     */
    HOLDER_OF_KEY("urn:oasis:names:tc:SAML:2.0:cm:holder-of-key"),


    /**
     * <h3>SAML 2.0 Profiles, Sender Vouches (section 3.2)</h3>
     * <p>
     * Indicates that no other information is available about the context of use of the assertion. The relying party
     * SHOULD utilize other means to determine if it should process the assertion further, subject to optional
     * constraints on confirmation using the attributes that MAY be present in the
     * &lt;SubjectConfirmationData&gt; element, as defined by [SAMLCore].
     * </p>
     */
    SENDER_VOUCHES("urn:oasis:names:tc:SAML:2.0:cm:sender-vouches"),


           /**
     * <h3>SAML 2.0 Profiles, Bearer (section 3.3)</h3>
     * <p>
     * The subject of the assertion is the bearer of the assertion, subject to optional constraints on confirmation
     * using the attributes that MAY be present in the <SubjectConfirmationData> element, as defined by
     * [SAMLCore].
     * </p>
     * <p>
     * Example: The bearer of the assertion can confirm itself as the subject, provided the assertion is delivered
     * in a message sent to "https://www.serviceprovider.com/saml/consumer" before 1:37 PM GMT on March
     * 19th, 2004, in response to a request with ID "_1234567890".     * </p>
     */
    BEARER("urn:oasis:names:tc:SAML:2.0:cm:bearer");


    private String mc;

    SubjectConfirmationMethod(String mc) {
        this.mc = mc;
    }

    public SubjectConfirmationMethod fromString(String m) {
        for (SubjectConfirmationMethod method : values()) {
            if (method.getValue().equals(m))
                return method;
        }

        throw new IllegalArgumentException("Invalid Subject Confirmation Method '"+m+"'");
    }

    public String getValue() {
        return mc;
    }


    @Override
    public String toString() {
        return mc;
    }
}
