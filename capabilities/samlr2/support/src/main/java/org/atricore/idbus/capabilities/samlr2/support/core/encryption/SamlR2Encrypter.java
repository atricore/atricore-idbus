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

package org.atricore.idbus.capabilities.samlr2.support.core.encryption;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.assertion.NameIDType;
import oasis.names.tc.saml._2_0.protocol.RequestAbstractType;
import oasis.names.tc.saml._2_0.protocol.StatusResponseType;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolver;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: SamlR2Encrypter.java 1381 2009-07-31 15:34:18Z chromy96 $
 */
public interface SamlR2Encrypter {

    /*
     * Encrypts a SAMLR2 Assertion
     */
    public EncryptedElementType encrypt ( AssertionType assertion ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( AssertionType assertion, SamlR2KeyResolver keyResolver ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( RequestAbstractType request ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( RequestAbstractType request, SamlR2KeyResolver keyResolver ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( StatusResponseType response ) throws SamlR2EncrypterException;

    public EncryptedElementType encrypt ( StatusResponseType response, SamlR2KeyResolver keyResolver ) throws SamlR2EncrypterException;

    public AssertionType decryptAssertion ( EncryptedElementType encryptedAssertion ) throws SamlR2EncrypterException;

    public AssertionType decryptAssertion ( EncryptedElementType encryptedAssertion, SamlR2KeyResolver keyResolver ) throws SamlR2EncrypterException;
    
    public NameIDType decryptNameID ( EncryptedElementType encryptedNameID ) throws SamlR2EncrypterException;
    
    public NameIDType decryptNameID ( EncryptedElementType encryptedNameID, SamlR2KeyResolver keyResolver ) throws SamlR2EncrypterException;
}
