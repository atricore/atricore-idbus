package com.atricore.idbus.console.licensing.main.impl;

import com.atricore.idbus.console.licensing.main.InvalidFeatureException;
import com.atricore.idbus.console.licensing.main.InvalidLicenseException;
import com.atricore.idbus.console.licensing.main.LicenseManager;
import com.atricore.idbus.console.licensing.main.ProductFeature;
import com.atricore.josso2.licensing._1_0.license.FeatureType;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import com.atricore.josso2.licensing._1_0.license.LicensedFeatureType;
import com.atricore.josso2.licensing._1_0.util.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.LogFactory;

import javax.xml.bind.*;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseManagerImpl implements LicenseManager {

    private static final Log logger = LogFactory.getLog(LicenseManagerImpl.class);

    // Encoded digital certificate (in the future, we should be able to manage a set of certs).
    //TODO replace with proper certificate
    private static final String certificate = "-----BEGIN CERTIFICATE-----\n" +
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
    
    private String licensePath = "etc/atricore.lic";

    private LicenseSigner signer;

    private Map<String, ProductFeature> productFeatures = new HashMap<String, ProductFeature>();

    public LicenseType activateLicense(byte[] license) throws InvalidLicenseException {
        LicenseType licenseType = null;
        try {            
            byte[] decoded = unzipAndDecodeLicense(license);
            //unmarshal
            licenseType = XmlUtils.unmarshallLicense(new ByteArrayInputStream(decoded), false);
            //and call validate
            validateLicense(licenseType);
            //store license
            storeLicense(license);

            return licenseType;
        } catch (JAXBException e) {
            logger.error("Error unmarshalling the license : " + e.getMessage(), e);
            throw new InvalidLicenseException(e);

        } catch (Exception e) {
            logger.error("Invalid License : " + e.getMessage(), e);
            throw new InvalidLicenseException(e);

        }
    }

    public LicenseType validateLicense(byte[] license) throws InvalidLicenseException {
        LicenseType licenseType = null;
        try {
            byte[] decoded = unzipAndDecodeLicense(license);
            //unmarshal
            licenseType = XmlUtils.unmarshallLicense(new ByteArrayInputStream(decoded), false);
            //and call validate
            validateLicense(licenseType);

            return licenseType;
        } catch (JAXBException e) {
            logger.error("Error unmarshalling the license : " + e.getMessage(), e);
            throw new InvalidLicenseException(e);

        } catch (Exception e) {
            logger.error("Invalid License : " + e.getMessage(), e);
            throw new InvalidLicenseException(e);

        }
    }

    public void validateCurrentLicense() throws InvalidLicenseException {
        try {
            // 1. Retrieve consoleLicense file
            LicenseType consoleLicense = getLicense();
            // 2. Validate
            validateLicense(consoleLicense);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InvalidLicenseException(e);
        }
    }

    public void validateFeature(String group, String name, String version) throws InvalidFeatureException {
        try {

            Calendar now = Calendar.getInstance();
            LicenseType lic = getLicense();

            boolean valid = false;

            for (LicensedFeatureType feature : lic.getLicensedFeature()) {

                FeatureType ft = feature.getFeature();

                if (ft.getGroup().equals(group) &&
                        ft.getName().equals(name)) {

                    // TODO : Check version range !
                    if (now.after(feature.getExpirationDate())) {
                        throw new InvalidFeatureException("Feature expired on " +
                                feature.getExpirationDate().toString());
                    }

                    valid = true;
                }

            }

            if (!valid)
                throw new InvalidFeatureException(group + "/" + name + "/" + version);

        } catch (Exception e) {
            throw new InvalidFeatureException(e);
        }
    }

    public LicenseType getLicense() throws InvalidLicenseException {
        try {
            return loadLicense();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InvalidLicenseException(e);
        }
    }

    public void registerFeature(ProductFeature productFeature) {
        this.productFeatures.put(productFeature.getId(), productFeature);
    }

    public void unregisterFeature(ProductFeature productFeature) {
        this.productFeatures.remove(productFeature.getId());
    }

    // -------------------------------------------------------< Protected Utils >

    protected byte[] unzipLicense(byte[] zippedLicense) throws InvalidLicenseException {
        try {
            ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(zippedLicense));
            ZipArchiveEntry zipEntry = zis.getNextZipEntry();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int c;
            while ((c = zis.read()) != -1) {
                bos.write(c);
            }

            return bos.toByteArray();
            
        } catch (IOException e) {
            throw new InvalidLicenseException("Cannot read license.");
        }
    }

    protected byte[] decodeLicense(byte[] encodedLicense) {
        return Base64.decodeBase64(encodedLicense);
    }

    protected byte[] unzipAndDecodeLicense(byte[] license) throws InvalidLicenseException {
        byte[] unzipped = unzipLicense(license);
        return decodeLicense(unzipped);        
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
        } catch(CertificateException e){
            if (logger.isDebugEnabled())
                logger.debug("Invalid License : " + e.getMessage(), e);

            throw new InvalidLicenseException(e);
        } catch (LicenseSignatureException e) {
            logger.error("Signature not valid:", e);
            throw new InvalidLicenseException(e);
        }
    }

    protected void validateLicenseInformation(LicenseType license) throws InvalidLicenseException {

        boolean valid = true;
        for (ProductFeature pf : productFeatures.values()) {
            try {
                validateFeature(pf.getGroup(), pf.getName(), pf.getVersion());
            } catch (InvalidFeatureException e) {
                logger.error(e.getMessage(),e);
                valid = false;
            }
        }

        if (!valid)
            throw new InvalidLicenseException("Product License is not valid");

    }

    protected LicenseType loadLicense() throws InvalidLicenseException {
        // Read consoleLicense from disk for now (we could use DB in the future!)
        LicenseType consoleLicense = null;
        File licenseFile = new File(licensePath);
        byte[] encodedContent = readLicenseFile();
        try {
            byte[] licenseContent = unzipAndDecodeLicense(encodedContent);
            consoleLicense = XmlUtils.unmarshallLicense(new ByteArrayInputStream(licenseContent), false);

        } catch (JAXBException e) {
            logger.error("Problem unmarshalling consoleLicense file : " + e.getMessage(), e);
            throw new InvalidLicenseException(e);
        } catch (FileNotFoundException e) {
            logger.error("License file not found : " + e.getMessage(), e);
            throw new InvalidLicenseException(e);
        } catch (Exception e) {
            throw new InvalidLicenseException(e);
        }
        return consoleLicense;
    }

    protected void storeLicense(byte[] license) throws InvalidLicenseException {
        try {
            FileOutputStream fos = new FileOutputStream(licensePath);
            fos.write(license);
            fos.close();
        } catch (FileNotFoundException e) {
            throw new InvalidLicenseException(e);
        } catch (IOException e) {
            throw new InvalidLicenseException(e);
        }
    }

    protected byte[] readLicenseFile() throws InvalidLicenseException {
        File licenseFile = new File(licensePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(licenseFile);
            byte[] content = new byte[(int)licenseFile.length()];
            fis.read(content);
            return content;
        } catch (FileNotFoundException e) {
            throw new InvalidLicenseException(e);
        } catch (IOException e) {
            throw new InvalidLicenseException(e);
        }

    }

//    protected void storeLicense(LicenseType license) throws InvalidLicenseException {
//        try {
//            String licenseString = XmlUtils.marshalLicense(license, false);
//            Writer out = new OutputStreamWriter(new FileOutputStream(licensePath));
//            out.write(licenseString);
//            out.close();
//        } catch (JAXBException e) {
//            logger.error("Error marshalling license", e);
//            throw new InvalidLicenseException(e);
//        } catch (Exception e) {
//            throw new InvalidLicenseException(e);
//        }
//    }

    public void setLicensePath(String licensePath) {
        this.licensePath = licensePath;
    }

    public String getLicensePath() {
        return licensePath;
    }

    public void setSigner(LicenseSigner signer) {
        this.signer = signer;
    }
}
