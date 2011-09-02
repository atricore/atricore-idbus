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

package org.atricore.idbus.capabilities.sso.support.test;

import oasis.names.tc.saml._2_0.assertion.AssertionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.utils.XMLUtils;
import org.atricore.idbus.capabilities.sso.support.core.SSOKeystoreKeyResolver;
import org.atricore.idbus.capabilities.sso.support.core.signature.JSR105SamlR2SignerImpl;
import org.junit.Test;
import org.w3c.dom.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Collections;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class XmlDsigTest {

    private static final Log logger = LogFactory.getLog(XmlDsigTest.class);

    /**
     * Sign a simple DOM document using the configured JSR 105 Provider
     */
    @Test
    public void simpleDocumentSign() throws Exception {

        //All the parameters for the keystore
        String keystoreType = "JKS";
        String keystoreFile = "src/test/resources/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";
        File signatureFile = new File("target/signature.xml");

        KeyStore ks = KeyStore.getInstance(keystoreType);
        FileInputStream fis = new FileInputStream(keystoreFile);

        //load the keystore
        ks.load(fis, keystorePass.toCharArray());

        //get the private key for signing.
        PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                privateKeyPass.toCharArray());

        X509Certificate cert = (X509Certificate) ks.getCertificate(certificateAlias);
        PublicKey publicKey = cert.getPublicKey();

        // Create a DOM XMLSignatureFactory that will be used to generate the
        // enveloped signature
        String providerName = System.getProperty
                ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
                (Provider) Class.forName(providerName).newInstance());

        // Create a Reference to the enveloped document (in this case we are
        // signing the whole document, so a URI of "" signifies that) and
        // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
        Reference ref = fac.newReference
                ("#12345", fac.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList
                                (fac.newTransform
                                        (Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null, null);

        // Create the SignedInfo
        SignedInfo si = fac.newSignedInfo
                (fac.newCanonicalizationMethod
                        (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null),
                        Collections.singletonList(ref));

        // Instantiate the document to be signed
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        //XML Signature needs to be namespace aware
        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();

        //Build a sample document. It will look something like:
        //<!-- Comment before -->
        //<apache:RootElement xmlns:apache="http://www.apache.org/ns/#app1" ID="12345">Some simple text
        //</apache:RootElement>
        //<!-- Comment after -->
        doc.appendChild(doc.createComment(" Comment before "));

        Element root = doc.createElementNS("http://www.apache.org/ns/#app1",
                "apache:RootElement");

        root.setAttributeNS(null, "ID", "12345");

        root.setAttributeNS(null, "attr1", "test1");
        root.setAttributeNS(null, "attr2", "test2");
        root.setAttributeNS(org.apache.xml.security.utils.Constants.NamespaceSpecNS, "xmlns:foo", "http://example.org/#foo");
        root.setAttributeNS("http://example.org/#foo", "foo:attr1", "foo's test");


        root.setAttributeNS(org.apache.xml.security.utils.Constants.NamespaceSpecNS, "xmlns:apache", "http://www.apache.org/ns/#app1");
        doc.appendChild(root);
        root.appendChild(doc.createTextNode("Some simple text\n"));

        // Create a DOMSignContext and specify the DSA PrivateKey and
        // location of the resulting XMLSignature's parent element
        DOMSignContext dsc = new DOMSignContext
                (privateKey, doc.getDocumentElement());

        // Create the XMLSignature (but don't sign it yet)
        KeyInfoFactory kif = fac.getKeyInfoFactory();

        X509Data kv = kif.newX509Data(Collections.singletonList(cert));

	    // Create a KeyInfo and add the KeyValue to it
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
        javax.xml.crypto.dsig.XMLSignature signature = fac.newXMLSignature(si, ki);

        signature.sign(dsc);


        // TODO : Verify signature ?

        // output the resulting document
        FileOutputStream f = new FileOutputStream(signatureFile);
        XMLUtils.outputDOMc14nWithComments(doc, f);
        f.close();

    }

    /**
     * Sign a SAMLR2 Assertion using the configured JSR 105 Provider
     */
    @Test
    public void assertionSign() throws Exception {
        //All the parameters for the keystore
        String keystoreType = "JKS";
        String keystoreFile = "src/test/resources/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";
        File assertionFile = new File("src/test/resources/assertion-001.xml");
        File signatureFile = new File("target/assertion-signed-001.xml");

        JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.assertion");
        Unmarshaller um = context.createUnmarshaller();

        JAXBElement jaxbElement = (JAXBElement) um.unmarshal(assertionFile);

        AssertionType assertion = (AssertionType) jaxbElement.getValue();

        // Unmarshall the assertion
        KeyStore ks = KeyStore.getInstance(keystoreType);
        FileInputStream fis = new FileInputStream(keystoreFile);

        //load the keystore
        ks.load(fis, keystorePass.toCharArray());

        //get the private key for signing.
        PrivateKey privateKey = (PrivateKey) ks.getKey(privateKeyAlias,
                privateKeyPass.toCharArray());

        X509Certificate cert = (X509Certificate) ks.getCertificate(certificateAlias);
        PublicKey publicKey = cert.getPublicKey();

        // Create a DOM XMLSignatureFactory that will be used to generate the
        // enveloped signature
        String providerName = System.getProperty
                ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
                (Provider) Class.forName(providerName).newInstance());

        // Create a Reference to the enveloped document (in this case we are
        // signing the whole document, so a URI of "" signifies that) and
        // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
        Reference ref = fac.newReference
                ("#" + assertion.getID(), fac.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList
                                (fac.newTransform
                                        (Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null, null);

        // Create the SignedInfo
        SignedInfo si = fac.newSignedInfo
                (fac.newCanonicalizationMethod
                        (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SignatureMethod.DSA_SHA1, null),
                        Collections.singletonList(ref));

        // Instantiate the document to be signed
        javax.xml.parsers.DocumentBuilderFactory dbf =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();

        //XML Signature needs to be namespace aware
        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();

        Marshaller m = context.createMarshaller();
        m.marshal(jaxbElement, doc);

        // Create a DOMSignContext and specify the DSA PrivateKey and
        // location of the resulting XMLSignature's parent element
        DOMSignContext dsc = new DOMSignContext
                (privateKey, doc.getDocumentElement(), doc.getDocumentElement().getFirstChild());

        // Create the XMLSignature (but don't sign it yet)
        KeyInfoFactory kif = fac.getKeyInfoFactory();

        X509Data kv = kif.newX509Data(Collections.singletonList(cert));

	    // Create a KeyInfo and add the KeyValue to it
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

        javax.xml.crypto.dsig.XMLSignature signature = fac.newXMLSignature(si, ki);

        signature.sign(dsc);
        // output the resulting document

        FileOutputStream f = new FileOutputStream(signatureFile);
        XMLUtils.outputDOMc14nWithComments(doc, f);
        f.close();
    }


    public void assertionWithSignerSign() throws Exception {

        //All the parameters for the keystore
        String keystoreType = "JKS";
        String keystoreFile = "src/test/resources/keystore.jks";
        String keystorePass = "xmlsecurity";
        String privateKeyAlias = "test";
        String privateKeyPass = "xmlsecurity";
        String certificateAlias = "test";
        File assertionFile = new File("src/test/resources/assertion-001.xml");
        File signatureFile = new File("target/assertion-signed-001.xml");

        JAXBContext context = JAXBContext.newInstance("oasis.names.tc.saml._2_0.assertion");
        Unmarshaller um = context.createUnmarshaller();

        JAXBElement jaxbElement = (JAXBElement) um.unmarshal(assertionFile);

        AssertionType assertion = (AssertionType) jaxbElement.getValue();

        JSR105SamlR2SignerImpl signer = new JSR105SamlR2SignerImpl();
//        signer.setKeystoreType(keystoreType);
//        signer.setKeystoreFile(keystoreFile);
//        signer.setKeystorePass(keystorePass);
//        signer.setPrivateKeyAlias(privateKeyAlias);
//        signer.setPrivateKeyPass(privateKeyPass);
//        signer.setCertificateAlias(certificateAlias);
        SSOKeystoreKeyResolver kr = new SSOKeystoreKeyResolver();
        kr.setCertificateAlias(certificateAlias);
        // TODO : kr.setKeystoreFile("keystore.jks");
        kr.setKeystorePass(keystorePass);
        kr.setKeystoreType(keystoreType);
        kr.setPrivateKeyAlias(privateKeyAlias);
        kr.setPrivateKeyPass(privateKeyPass);
        signer.setKeyResolver( kr );


        signer.init();

        assertion = signer.sign(assertion);

        // TODO : Provide MD
        signer.validate(null, assertion);


    }



}
