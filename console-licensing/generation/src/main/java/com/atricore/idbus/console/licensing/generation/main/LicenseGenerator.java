package com.atricore.idbus.console.licensing.generation.main;

import com.atricore.idbus.console.licensing.generation.LicenseGenerationException;
import com.atricore.idbus.console.licensing.main.util.LicenseUtil;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Collections;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseGenerator {

    static Log logger = LogFactory.getLog(LicenseGenerator.class);

    public LicenseType generate(String license, String keystoreFile, String keystorePass, String keyName, String keyPass, String certAlias) throws LicenseGenerationException {
        LicenseType consoleLicense = null;
        PrivateKey privateKey = null;
        Certificate certificate = null;
        File licenseFile = new File(license);
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            if (keystoreFile == null)
                throw new IllegalStateException("No keystore resource defined!");
            
            FileInputStream fis = new FileInputStream(keystoreFile);
            ks.load(fis, keystorePass.toCharArray());

            if (certAlias != null)
                certificate = ks.getCertificate(certAlias);            

            if (keyName != null)
                privateKey = (PrivateKey) ks.getKey(keyName, keyPass != null ? keyPass.toCharArray() : null);

            LicenseType licenseType = LicenseUtil.unmarshal(new FileInputStream(licenseFile));

            // Instantiate the document to be signed
            javax.xml.parsers.DocumentBuilderFactory dbf =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance();

            // XML Signature needs to be namespace aware
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(new FileInputStream(licenseFile));

            org.w3c.dom.Document signedDoc = sign(doc, certificate, privateKey);

            JAXBContext ctx = JAXBContext.newInstance(LicenseUtil.ATRICORE_LICENSE_PKG, Thread.currentThread().getContextClassLoader());
            Unmarshaller u = ctx.createUnmarshaller();
            JAXBElement<LicenseType> jaxbLicense = (JAXBElement<LicenseType>) u.unmarshal(doc);

            //TODO REMOVE - saves the license as new file
            LicenseUtil.marshal(jaxbLicense.getValue(), license + "_signed.xml");
            
            return jaxbLicense.getValue();

        } catch (FileNotFoundException e) {
            logger.error("License file not found", e);
            throw new LicenseGenerationException(e);
        } catch (CertificateException e) {
            throw new LicenseGenerationException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new LicenseGenerationException(e);
        } catch (KeyStoreException e) {
            throw new LicenseGenerationException(e);
        } catch (IOException e) {
            throw new LicenseGenerationException(e);
        } catch (UnrecoverableKeyException e) {
            throw new LicenseGenerationException(e);
        } catch (ParserConfigurationException e) {
            throw new LicenseGenerationException(e);
        } catch (SAXException e) {
            logger.error("Problem unmarshalling license file", e);
            throw new LicenseGenerationException(e);
        } catch (JAXBException e) {
            throw new LicenseGenerationException(e);
        }
    }

    LicenseType generate(LicenseType unsigned) {
        return null;
    }

    protected Document sign(Document doc, Certificate cert, PrivateKey privateKey) throws LicenseGenerationException {
        try {
            // Create a DOM XMLSignatureFactory that will be used to generate the
            // enveloped signature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", new org.jcp.xml.dsig.internal.dom.XMLDSigRI());

            if (logger.isDebugEnabled())
                logger.debug("Creating XML DOM Digital Signature (not signing yet!)");

            // Create a Reference to the enveloped document and
            // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
            // The URI must be the assertion ID
            Reference ref = fac.newReference
                    ("", fac.newDigestMethod(DigestMethod.SHA1, null),
                            Collections.singletonList
                                    (fac.newTransform
                                            (Transform.ENVELOPED, (TransformParameterSpec) null)),
                            null, null);

            // Use signature method based on key algorithm.
            String signatureMethod = SignatureMethod.DSA_SHA1;
            if (privateKey.getAlgorithm().equals("RSA"))
                signatureMethod = SignatureMethod.RSA_SHA1;

            logger.debug("Using signature method " + signatureMethod);

            // Create the SignedInfo, with the X509 Certificate
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                            (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                                    (C14NMethodParameterSpec) null),
                            fac.newSignatureMethod(signatureMethod, null),
                            Collections.singletonList(ref));

            // Create a KeyInfo and add the Certificate to it
            KeyInfoFactory kif = fac.getKeyInfoFactory();

            X509Data kv = kif.newX509Data(Collections.singletonList(cert));
            //KeyValue kv = kif.newKeyValue(keyResolver.getCertificate().getPublicKey());

            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));
            javax.xml.crypto.dsig.XMLSignature signature = fac.newXMLSignature(si, ki);

            if (logger.isDebugEnabled())
                logger.debug("Signing...");

            // Create a DOMSignContext and specify the DSA PrivateKey and
            // location of the resulting XMLSignature's parent element
            DOMSignContext dsc = new DOMSignContext
                    (privateKey, doc.getDocumentElement(), doc.getDocumentElement().getFirstChild());

            // Sign the assertion
            signature.sign(dsc);

            if (logger.isDebugEnabled())
                logger.debug("Signing... DONE!");

            return doc;


        } catch (NoSuchAlgorithmException e) {
            throw new LicenseGenerationException(e.getMessage(), e);
        } catch (XMLSignatureException e) {
            throw new LicenseGenerationException(e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new LicenseGenerationException(e.getMessage(), e);
        } catch (MarshalException e) {
            throw new LicenseGenerationException(e.getMessage(), e);
        }
    }
}
