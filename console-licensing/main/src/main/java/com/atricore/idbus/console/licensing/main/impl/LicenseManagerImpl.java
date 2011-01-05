package com.atricore.idbus.console.licensing.main.impl;

import com.atricore.idbus.console.licensing.main.InvalidFeatureException;
import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import com.atricore.josso2.licensing._1_0.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Iterator;
import javax.xml.crypto.dsig.*;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseManagerImpl implements LicenseManager {

    static Log logger = LogFactory.getLog(LicenseManagerImpl.class);

    // Encoded digital certificate (in the future, we should be able to manage a set of certs).
    //TODO replace with proper certificate
    private String certificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDVzCCAj8CBE0cdYswDQYJKoZIhvcNAQEEBQAwcDEQMA4GA1UEBhMHVW5rbm93\n" +
            "bjEQMA4GA1UECBMHVW5rbm93bjEQMA4GA1UEBxMHVW5rbm93bjEQMA4GA1UEChMH\n" +
            "VW5rbm93bjEQMA4GA1UECxMHVW5rbm93bjEUMBIGA1UEAxMLRGVqYW4gTWFyaWMw\n" +
            "HhcNMTAxMjMwMTIwNTMxWhcNMTMwMTI4MTIwNTMxWjBwMRAwDgYDVQQGEwdVbmtu\n" +
            "b3duMRAwDgYDVQQIEwdVbmtub3duMRAwDgYDVQQHEwdVbmtub3duMRAwDgYDVQQK\n" +
            "EwdVbmtub3duMRAwDgYDVQQLEwdVbmtub3duMRQwEgYDVQQDEwtEZWphbiBNYXJp\n" +
            "YzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALCD0c4gN9akBO/crFpk\n" +
            "PpqFNNRg9fpnDrW3VRHdGtZCUWc0Bd6H3KisSPOAP7LChzFVjpbNK914naSVbzfX\n" +
            "z7oYEGGJ7RMueRgHRxkeud4aUAZEfVqAcsX2a5Xuy2ghf4zaz0G/GI0mI0TTKlnh\n" +
            "yp1h3g6ImO86UCzUXI954KvKy6aEj8SEc53kmyRdsErypAOoKQVH3DqJ8A2CkMMU\n" +
            "JhbawH3yGhayxO070vCcBlZPwWCz23GWO3XjHdlbS7HHxT4XJs3VMWDWA1qO8+bV\n" +
            "wu0RxYJY9Dbigi8vd4hHfimfMq0qk0FwC+M+pD06iRFhGotc1d+yCCITTcDlu4p9\n" +
            "auUCAwEAATANBgkqhkiG9w0BAQQFAAOCAQEABSMuhGIkM+WlFT9Mu7qZHwF4oFpf\n" +
            "A0TT7SMwyac4vJXjSsXX28n3htP5xN95m17Fi8n4VVqY8yQPLKHDD4Id6ffaM8h7\n" +
            "hwgrmy66pAjsU0LNnvL76+md/CRm/3XRAVG2Tj7pEBQ0lS86HJMcvEcdC38Ng/+q\n" +
            "/mpi8sH3VgufF8ooCYlPPLEk55Nc+GZkXj7j6yd4ocgPGqdiB7L2CpC82s6gb1Ju\n" +
            "mvbxZJj5s1rzxDTAm5WYDV50wqnWvgIyjKd4ymeoQQnGfunu31OZ9xcpdGgLoH6V\n" +
            "OVuOVWYfBNMwMNikL2JvOZAmNNFWZia4EGphS8Uh+yLRhHbpobRqT6+DOg==\n" +
            "-----END CERTIFICATE-----";
    
    private String licensePath = "";
    private LicenseSigner signer;


    public void activateLicense(byte[] license) throws InvalidLicenseException {
        LicenseType licenseType = null;
        try {
            //unmarshal
            licenseType = XmlUtils.unmarshallLicense(new ByteArrayInputStream(license), false);
            //and call validate
            validateLicense(licenseType);
            //store license
            storeLicense(licenseType);
        } catch (JAXBException e) {
            logger.error("Error unmarshalling the license", e);
            throw new InvalidLicenseException(e);
        } catch (Exception e) {
            throw new InvalidLicenseException(e);
        }
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
        validateSignature(license, certificate);

        // 2. Validate consoleLicense information
        validateLicenseInformation(license);

    }

    protected void validateSignature(LicenseType license, String certificate) throws InvalidLicenseException {
        try{
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            java.security.cert.Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(certificate.getBytes()));

            LicenseKeyResolver keyResolver = new LicenseKeyResolverImpl(cert, null); //we don't need private key in resolver to validate license
            signer.validate(license, keyResolver);
        }catch(CertificateException e){
            throw new InvalidLicenseException(e);
        } catch (LicenseSignatureException e) {
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
            consoleLicense = XmlUtils.unmarshallLicense(new FileInputStream(licenseFile), false);
        } catch (JAXBException e) {
            logger.error("Problem unmarshalling consoleLicense file", e);
            throw new InvalidLicenseException(e);
        } catch (FileNotFoundException e) {
            logger.error("License file not found", e);
            throw new InvalidLicenseException(e);
        } catch (Exception e) {
            throw new InvalidLicenseException(e);
        }
        return consoleLicense;
    }

    protected void storeLicense(LicenseType license) throws InvalidLicenseException {
        try {
            String licenseString = XmlUtils.marshalLicense(license, false);
            Writer out = new OutputStreamWriter(new FileOutputStream(licensePath));
            out.write(licenseString);
            out.close();
        } catch (JAXBException e) {
            logger.error("Error marshalling license", e);
            throw new InvalidLicenseException(e);
        } catch (Exception e) {
            throw new InvalidLicenseException(e);
        }
    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public void setSigner(LicenseSigner signer) {
        this.signer = signer;
    }
}
