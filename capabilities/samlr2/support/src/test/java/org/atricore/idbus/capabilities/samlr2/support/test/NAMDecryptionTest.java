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
import org.apache.xml.security.utils.XMLUtils;
import org.atricore.idbus.capabilities.samlr2.support.SAMLR2Constants;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolver;
import org.atricore.idbus.capabilities.samlr2.support.core.SamlR2KeyResolverImpl;
import org.atricore.idbus.capabilities.samlr2.support.core.encryption.XmlSecurityEncrypterImpl;
import org.junit.Test;
import org.w3c.dom.Document;

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
import java.security.cert.X509Certificate;
import java.util.Enumeration;

/**
 * @author <a href=mailto:ajadzinsky@atricore.org>Alejandro Jadzinsky</a>
 *         User: ajadzinsky
 *         Date: Jun 8, 2009
 */
public class NAMDecryptionTest {
    static Log logger = LogFactory.getLog( NAMDecryptionTest.class.getName() );

    @Test
    public void testEncryptedAssertion () throws Exception {
        Document document = loadEncryptedDocument("src/test/resources/nam-idp-encrypted-assertion.xml");
        
        JAXBContext context = JAXBContext.newInstance( SAMLR2Constants.SAML_ASSERTION_PKG );
        Unmarshaller u = context.createUnmarshaller();
        JAXBElement<EncryptedElementType> eet = u.unmarshal( document, EncryptedElementType.class );

        XmlSecurityEncrypterImpl encrypter = new XmlSecurityEncrypterImpl();
        encrypter.setKeyResolver( getKeyResolver() );
        AssertionType at = encrypter.decryptAssertion( eet.getValue() );

        assert at != null : "No Assertion decrypted";

        JAXBElement<AssertionType> jaxbAssertion =
                new JAXBElement<AssertionType>(
                        new QName( SAMLR2Constants.SAML_ASSERTION_NS, "Assertion" ),
                        AssertionType.class,
                        at );

        document.removeChild( document.getDocumentElement() );
        context.createMarshaller().marshal( jaxbAssertion, document );

        outputDocToFile( document, "target/nam-idp-decrypted-assertion.xml" );

    }

    private Document loadEncryptedDocument (String fileName) throws Exception {
        File encryptionFile = new File( fileName );
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware( true );

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse( encryptionFile );
    }

    private Key getKey () throws Exception {
        String keystoreType = "pkcs12";
        String keystoreFile = "src/test/resources/sp_encryption_exportedCert.pfx";
        String keystorePass = "Josso2";
        String KeyAlias = "test-encryption";

        KeyStore ks = KeyStore.getInstance( keystoreType );
        FileInputStream fis = new FileInputStream( keystoreFile );

        //load the keystore
        ks.load( fis, keystorePass.toCharArray() );
        logger.debug( "type: " + ks.getType() );
        logger.debug( "provider: " + ks.getProvider().getInfo() );

        Enumeration<String> aliases = ks.aliases();
        for ( ; aliases.hasMoreElements(); ) {
            String alias = aliases.nextElement();
            logger.debug( alias );
            Key priKey = ks.getKey( alias, "Josso2".toCharArray() );
            logger.debug( "private key: " + priKey );
            X509Certificate cert = (X509Certificate) ks.getCertificate( alias );
            logger.debug( "cert : " + cert );
            Key pubKey = cert.getPublicKey();
            logger.debug( "public key: " + pubKey );
        }

        return ks.getKey( KeyAlias, keystorePass.toCharArray() );

    }

    private SamlR2KeyResolver getKeyResolver () throws Exception {
        String keystoreType = "pkcs12";
        String keystoreFile = "src/test/resources/sp_encryption_exportedCert.pfx";
        String keystorePass = "Josso2";
        String privateKeyAlias = "test-encryption";
        String privateKeyPass = "Josso2";

        KeyStore ks = KeyStore.getInstance( keystoreType );
        FileInputStream fis = new FileInputStream( keystoreFile );

        //load the keystore
        ks.load( fis, keystorePass.toCharArray() );
        Key key = ks.getKey( privateKeyAlias, privateKeyPass.toCharArray() );
        logger.debug( "loaded private key: " + ( key != null ) );

        SamlR2KeyResolverImpl resolver = new SamlR2KeyResolverImpl( (PrivateKey)key );
        return resolver;
    }


    private static void outputDocToFile ( Document doc, String fileName )
            throws Exception {
        File encryptionFile = new File( fileName );
        FileOutputStream f = new FileOutputStream( encryptionFile );
        XMLUtils.outputDOMc14nWithComments( doc, f );
        f.close();
    }
}
