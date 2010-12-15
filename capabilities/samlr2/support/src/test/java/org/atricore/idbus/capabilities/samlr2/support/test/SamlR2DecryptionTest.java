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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.utils.EncryptionConstants;
import org.apache.xml.security.utils.XMLUtils;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolver;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolverImpl;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.XmlSecurityEncrypterImpl;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;

/**
 * @author <a href=mailto:ajadzinsky@atricore.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Jun 4, 2009
 */
public class SamlR2DecryptionTest {
    static Log logger = LogFactory.getLog( SamlR2DecryptionTest.class.getName() );

//    static {
//        org.apache.xml.security.Init.init();
//    }

    @Test
    public void test () throws Exception {
        Document document = loadEncryptedDocument();

        JAXBContext context = JAXBContext.newInstance( SAMLR2Constants.SAML_ASSERTION_PKG );
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<EncryptedElementType> eet = (JAXBElement<EncryptedElementType>) u.unmarshal( document );


        XmlSecurityEncrypterImpl encrypter = new XmlSecurityEncrypterImpl();
        encrypter.setKeyResolver( getKeyResolver() );
        AssertionType at = encrypter.decryptAssertion( eet.getValue() );

        assert at != null : "No Assertion decrypted";
        assert at.getID().equals( "6FBE120C92DAED61" ) : "ID missmatch error";
        assert at.getIssuer().getValue().equals( "http://idp.atricore.com" ) : "Issuer missmatch error";

        JAXBElement<AssertionType> jaxbAssertion =
                new JAXBElement<AssertionType>(
                        new QName( SAMLR2Constants.SAML_ASSERTION_NS, "Assertion" ),
                        AssertionType.class,
                        at );

        document.removeChild( document.getDocumentElement() );
        context.createMarshaller().marshal( jaxbAssertion, document );

        outputDocToFile( document );
    }

    //    @Test
    public void testDecryptAssertion () throws Exception {
        Document document = loadEncryptedDocument();

        Element encryptedDataElement =
                (Element) document.getElementsByTagNameNS(
                        EncryptionConstants.EncryptionSpecNS,
                        EncryptionConstants._TAG_ENCRYPTEDDATA ).item( 0 );


        /*
        * Load the key to be used for decrypting the xml data
        * encryption key.
        */
        Key kek = loadKeyEncryptionKey( document );
        logger.debug( "loaded kek algorithm: " + kek.getAlgorithm() );
        logger.debug( "loaded kek format: " + kek.getFormat() );

        XMLCipher xmlCipher = XMLCipher.getInstance();
        xmlCipher.init( XMLCipher.DECRYPT_MODE, kek );

        /*
         * The following doFinal call replaces the encrypted data with
         * decrypted contents in the document.
         */
        xmlCipher.doFinal( document, encryptedDataElement );

        outputDocToFile( document );
    }

    private Document loadEncryptedDocument () throws Exception {

        String fileName = "src/test/resources/assertion-encrypted-001.xml";
        File encryptionFile = new File( fileName );
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse( encryptionFile );

        return document;
    }

    private Key loadKeyEncryptionKey ( Document document ) throws Exception {
        Element encryptedKeyElement =
                (Element) document.getElementsByTagNameNS(
                        EncryptionConstants.EncryptionSpecNS,
                        EncryptionConstants._TAG_ENCRYPTEDKEY ).item( 0 );

        XMLCipher keyCipher = XMLCipher.getInstance();
        keyCipher.init( XMLCipher.UNWRAP_MODE, getKeyResolver().getPrivateKey() );
        EncryptedKey ek = keyCipher.loadEncryptedKey( document, encryptedKeyElement );
        assert ek != null : "No encryptedKey found";

        Element encryptedDataElement =
                (Element) document.getElementsByTagNameNS(
                        EncryptionConstants.EncryptionSpecNS,
                        EncryptionConstants._TAG_ENCRYPTEDDATA ).item( 0 );

        Element encryptionMethodElem =
                (Element) encryptedDataElement.getElementsByTagName(
                        EncryptionConstants._TAG_ENCRYPTIONMETHOD ).item( 0 );
        String algoritmUri = encryptionMethodElem.getAttribute( EncryptionConstants._ATT_ALGORITHM );
        logger.debug( "algoritmUri: " + algoritmUri );

        return keyCipher.decryptKey( ek, algoritmUri );

    }

    private SamlR2KeyResolver getKeyResolver () throws Exception {
        String keystoreType = "JKS";
        String keystoreFile = "src/test/resources/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "rsa-keys";
        String privateKeyPass = "rsa-keys-pwd";

        KeyStore ks = KeyStore.getInstance( keystoreType );
        FileInputStream fis = new FileInputStream( keystoreFile );

        //load the keystore
        ks.load( fis, keystorePass.toCharArray() );
        Key key = ks.getKey( privateKeyAlias, privateKeyPass.toCharArray() );
        logger.debug( "loaded private key: " + ( key != null ) );

        SamlR2KeyResolverImpl resolver = new SamlR2KeyResolverImpl( (PrivateKey)key );
        return resolver;
    }

    private static void outputDocToFile ( Document doc )
            throws Exception {
        File encryptionFile = new File( "target/assertion-decrypted-001.xml" );
        FileOutputStream f = new FileOutputStream( encryptionFile );
        XMLUtils.outputDOMc14nWithComments( doc, f );
        f.close();
    }

}
