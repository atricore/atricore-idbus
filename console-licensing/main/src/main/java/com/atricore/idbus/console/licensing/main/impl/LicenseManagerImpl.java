package com.atricore.idbus.console.licensing.main.impl;

import com.atricore.idbus.console.licensing.main.InvalidFeatureException;
import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.idbus.console.licensing.main.util.LicenseUtil;
import com.atricore.idbus.console.licensing.main.util.NamespaceFilterXMLStreamWriter;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.crypto.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.security.Key;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import javax.xml.crypto.dsig.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseManagerImpl implements LicenseManager {

    static Log logger = LogFactory.getLog(LicenseManagerImpl.class);

    // Encoded digital certificate (in the future, we should be able to manage a set of certs).
    private String publicKey = "";
    private String licensePath = "";

    public void activateLicense(byte[] license) throws InvalidLicenseException {
        LicenseType licenseType = null;
        try {
            licenseType = LicenseUtil.unmarshal(new ByteArrayInputStream(license));
        } catch (JAXBException e) {
            logger.error("Error unmarshalling the license", e);
            throw new InvalidLicenseException(e);
        }
        validateLicense(licenseType);
        // 1. Unmarshal and call validate again
    }

    public void activateLicense(LicenseType license) throws InvalidLicenseException {
        // 1. Validate consoleLicense
        validateLicense(license);
        // 3. Store consoleLicense file in etc (DB in the future ?)
        storeLicense(license);
    }

    public void validateLicense() throws InvalidLicenseException {
        // 1. Retrieve consoleLicense file
        LicenseType consoleLicense = getLicense();
        // 2. Validate
        validateLicense(consoleLicense);
    }

    public void validateFeature(String group, String name) throws InvalidFeatureException {

    }

    public LicenseType getLicense() throws InvalidLicenseException {
        return loadLicense();
    }

    protected void validateLicense(LicenseType license) throws InvalidLicenseException {
        // 1. Validate signature
        validateSignature(license, publicKey);

        // 2. Validate consoleLicense information
        validateLicenseInformation(license);

    }

    protected void validateSignature(LicenseType license, String certificate) throws InvalidLicenseException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();

            JAXBContext context = JAXBContext.newInstance(LicenseUtil.ATRICORE_LICENSE_PKG, license.getClass().getClassLoader());
            Marshaller m = context.createMarshaller();
            JAXBElement<LicenseType> jaxbAssertion = new JAXBElement<LicenseType>(new QName(LicenseUtil.ATRICORE_LICENSE_NS, "License"), LicenseType.class, license);
            StringWriter swas = new StringWriter();
            XMLStreamWriter sw = new NamespaceFilterXMLStreamWriter(swas);
            m.marshal(jaxbAssertion, sw);
            Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(swas.toString().getBytes()));

            // Find Signature element
            NodeList nl =
                    doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                throw new InvalidLicenseException("Cannot find Signature element");
            }

            // Create a DOM XMLSignatureFactory that will be used to unmarshal the
            // document containing the XMLSignature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

            //load public key from cert file
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            java.security.cert.Certificate cert = certFactory.generateCertificate(new FileInputStream(publicKey));

            // Validate all Signature elements
            for (int k = 0; k < nl.getLength(); k++) {

//                DOMValidateContext valContext = new DOMValidateContext
//                        (new RawX509KeySelector(), nl.item(k));
                
                DOMValidateContext valContext = new DOMValidateContext
                        (cert.getPublicKey(), nl.item(k));
                

                // unmarshal the XMLSignature
                XMLSignature signature = fac.unmarshalXMLSignature(valContext);

                // Validate the XMLSignature (generated above)
                boolean coreValidity = signature.validate(valContext);

                // Check core validation status
                if (!coreValidity) {
                    logger.debug("Signature failed core validation");
                    boolean sv = signature.getSignatureValue().validate(valContext);
                    logger.debug("signature validation status: " + sv);
                    // check the validation status of each Reference
                    Iterator i = signature.getSignedInfo().getReferences().iterator();
                    boolean refValid = true;
                    for (int j = 0; i.hasNext(); j++) {
                        boolean b = ((Reference) i.next()).validate(valContext);
                        if (!b) refValid = b;
                        logger.debug("ref[" + j + "] validity status: " + b);
                    }
                    throw new InvalidLicenseException("Signature failed core validation" + (refValid ? " but passed all Reference validations" : " and some/all Reference validation"));
                }
            }
        } catch (ParserConfigurationException e) {
            throw new InvalidLicenseException(e);
        } catch (XMLSignatureException e) {
            throw new InvalidLicenseException(e);
        } catch (MarshalException e) {
            throw new InvalidLicenseException(e);
        } catch (JAXBException e) {
            throw new InvalidLicenseException(e);
        } catch (IOException e) {
            throw new InvalidLicenseException(e);
        } catch (XMLStreamException e) {
            throw new InvalidLicenseException(e);
        } catch (SAXException e) {
            throw new InvalidLicenseException(e);
        } catch (CertificateException e) {
            throw new InvalidLicenseException(e);
        }
    }

    protected void validateLicenseInformation(LicenseType license) throws InvalidLicenseException {
        // TODO : Validate expiration date, distribution type, etc.
    }

    protected LicenseType loadLicense() throws InvalidLicenseException {
        // Read consoleLicense from disk for now (we could use DB in the future!)
        LicenseType consoleLicense = null;
        File licenseFile = new File(licensePath);
        try {
            consoleLicense = LicenseUtil.unmarshal(new FileInputStream(licenseFile));
        } catch (JAXBException e) {
            logger.error("Problem unmarshalling consoleLicense file", e);
            throw new InvalidLicenseException(e);
        } catch (FileNotFoundException e) {
            logger.error("License file not found", e);
            throw new InvalidLicenseException(e);
        }
        return consoleLicense;
    }

    protected void storeLicense(LicenseType license) throws InvalidLicenseException {
        try {
            LicenseUtil.marshal(license, licensePath);
        } catch (JAXBException e) {
            logger.error("Error marshalling license", e);
            throw new InvalidLicenseException(e);
        }
    }

    /**
     * KeySelector which would retrieve the X509Certificate out of the
     * KeyInfo element and return the public key.
     * NOTE: If there is an X509CRL in the KeyInfo element, then revoked
     * certificate will be ignored.
     */
    public static class RawX509KeySelector extends KeySelector {

        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            // search for X509Data in keyinfo
            Iterator iter = keyInfo.getContent().iterator();
            while (iter.hasNext()) {
                XMLStructure kiType = (XMLStructure) iter.next();
                if (kiType instanceof X509Data) {
                    X509Data xd = (X509Data) kiType;
                    Object[] entries = xd.getContent().toArray();
                    X509CRL crl = null;
                    // Looking for CRL before finding certificates
                    for (int i = 0; (i < entries.length && crl != null); i++) {
                        if (entries[i] instanceof X509CRL) {
                            crl = (X509CRL) entries[i];
                        }
                    }
                    Iterator xi = xd.getContent().iterator();
                    boolean hasCRL = false;
                    while (xi.hasNext()) {
                        Object o = xi.next();
                        // skip non-X509Certificate entries
                        if (o instanceof X509Certificate) {
                            if ((purpose != KeySelector.Purpose.VERIFY) &&
                                    (crl != null) &&
                                    crl.isRevoked((X509Certificate) o)) {
                                continue;
                            } else {
                                return new SimpleKeySelectorResult
                                        (((X509Certificate) o).getPublicKey());
                            }
                        }
                    }
                }
            }
            throw new KeySelectorException("No X509Certificate found!");
        }
    }

    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() {
            return pk;
        }
    }    

}
