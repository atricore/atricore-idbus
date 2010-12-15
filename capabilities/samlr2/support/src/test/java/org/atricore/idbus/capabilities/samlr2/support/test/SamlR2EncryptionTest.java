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

package org.atricore.idbus.capabilities.samlr2.support.test;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import oasis.names.tc.saml._2_0.assertion.EncryptedElementType;
import oasis.names.tc.saml._2_0.assertion.ObjectFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.XMLUtils;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeystoreKeyResolver;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.XmlSecurityEncrypterImpl;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @author <a href=mailto:ajadzinsky@atricore.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Jun 2, 2009
 */
public class SamlR2EncryptionTest {
    static Log logger = LogFactory.getLog( SamlR2EncryptionTest.class.getName() );

    @Test
    public void testEncriptAssertion () throws Exception {
        /*
        String keystoreType = "JKS";
        String keystoreFile = "src/test/resources/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "rsa-keys";

        KeyStore ks = KeyStore.getInstance( keystoreType );
        FileInputStream fis = new FileInputStream( keystoreFile );

        //load the keystore
        ks.load( fis, keystorePass.toCharArray() );
        X509Certificate cert = (X509Certificate) ks.getCertificate( privateKeyAlias );
        Key pubKey = cert.getPublicKey();
        */

        SamlR2KeystoreKeyResolver keyResolver = new SamlR2KeystoreKeyResolver();
        keyResolver.setKeystoreType( "JKS" );
        // TODO : keyResolver.setKeystoreFile( "keystore.jks" );
        keyResolver.setKeystorePass( "xmlsecurity" );
        keyResolver.setCertificateAlias( "rsa-keys" );
        keyResolver.init();

        XmlSecurityEncrypterImpl encrypter = new XmlSecurityEncrypterImpl();
        encrypter.setSymmetricKeyAlgorithmURI( XMLCipher.AES_128 );
        encrypter.setKeyResolver( keyResolver );
        encrypter.setKekAlgorithmURI( XMLCipher.RSA_v1dot5 );

        AssertionType at = getAssertion();
        assert at != null : "Error loading assertion";
        EncryptedElementType eet = encrypter.encrypt( at );
        assert eet != null;

        ObjectFactory of = new ObjectFactory();
        JAXBElement<EncryptedElementType> encAssertionElement = of.createEncryptedAssertion( eet );
        javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        JAXBContext context = JAXBContext.newInstance( SAMLR2Constants.SAML_ASSERTION_PKG );
        Marshaller m = context.createMarshaller();
        m.marshal( encAssertionElement, doc );

        outputDocToFile( doc );

    }

    private AssertionType getAssertion () throws Exception {
        FileInputStream is = new FileInputStream( "src/test/resources/assertion-001.xml" );
        JAXBContext context = JAXBContext.newInstance( SAMLR2Constants.SAML_ASSERTION_PKG );
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<AssertionType> ate = (JAXBElement<AssertionType>) u.unmarshal( is );
        return ate.getValue();
    }

    private static void outputDocToFile ( Document doc )
            throws Exception {
        File encryptionFile = new File( "target/assertion-encrypted-001.xml" );
        FileOutputStream f = new FileOutputStream( encryptionFile );
        XMLUtils.outputDOMc14nWithComments( doc, f );
        f.close();
    }
}
