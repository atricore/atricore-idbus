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

package org.atricore.idbus.capabilities.sso.support.core.encryption;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.metadata.KeyDescriptorType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeyResolver;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Encrypter.java 1381 2009-07-31 15:34:18Z chromy96 $
 */
public interface SamlR2Encrypter {

    String ENC_XML_NS = "http://www.w3.org/2001/04/xmlenc#";
    
    String ENC_DATA_ENC_METHOD_3DES =
            "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    String ENC_DATA_ENC_METHOD_AES_128 =
            "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    String ENC_DATA_ENC_METHOD_AES_256 =
            "http://www.w3.org/2001/04/xmlenc#aes256-cbc";

    String ENC_KEY_ENC_METHOD_RSA_1_5 =
            "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    String ENC_KEY_ENC_METHOD_RSA_OAEP =
            "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    String ENC_KEY_ENC_METHOD_3DES =
            "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    String ENC_KEY_ENC_METHOD_AES_128 =
            "http://www.w3.org/2001/04/xmlenc#kw-aes128";
    String ENC_KEY_ENC_METHOD_AES_256 =
            "http://www.w3.org/2001/04/xmlenc#kw-aes256";


    /*
     * Encrypts a SAMLR2 Assertion
     */
    public EncryptedElementType encrypt ( AssertionType assertion, KeyDescriptorType key, String dataEncryptionAlgorithm) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( AssertionType assertion, KeyDescriptorType key, String dataEncryptionAlgorithm, SSOKeyResolver keyResolver ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( RequestAbstractType request, KeyDescriptorType key ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( RequestAbstractType request, KeyDescriptorType key, SSOKeyResolver keyResolver ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( StatusResponseType response, KeyDescriptorType key ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( StatusResponseType response, KeyDescriptorType key, SSOKeyResolver keyResolver ) throws SamlR2EncrypterException;

    public AssertionType decryptAssertion ( EncryptedElementType encryptedAssertion ) throws SamlR2EncrypterException;

    public Document decryptAssertionAsDOM ( EncryptedElementType encryptedAssertion ) throws SamlR2EncrypterException;

    public AssertionType decryptAssertion ( EncryptedElementType encryptedAssertion, SSOKeyResolver keyResolver ) throws SamlR2EncrypterException;

    public Document decryptAssertionAsDOM ( EncryptedElementType encryptedAssertion, SSOKeyResolver keyResolver ) throws SamlR2EncrypterException;

    public NameIDType decryptNameID ( EncryptedElementType encryptedNameID ) throws SamlR2EncrypterException;
    
    public NameIDType decryptNameID ( EncryptedElementType encryptedNameID, SSOKeyResolver keyResolver ) throws SamlR2EncrypterException;

}
