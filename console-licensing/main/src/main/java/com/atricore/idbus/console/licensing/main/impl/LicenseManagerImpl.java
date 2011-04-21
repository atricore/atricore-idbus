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

    // JOSSO 2 Licensing certificate :
    private static final String certificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDITCCAt+gAwIBAgIETW5/vzALBgcqhkjOOAQDBQAwdDELMAkGA1UEBhMCVVMxCzAJBgNVBAgT\n" +
            "Ak5ZMREwDwYDVQQHEwhOZXcgWW9yazEXMBUGA1UEChMOQXRyaWNvcmUsIEluYy4xGjAYBgNVBAsT\n" +
            "EUF0cmljb3JlIFNlcnZpY2VzMRAwDgYDVQQDEwdKT1NTTyAyMB4XDTExMDMwMjE3MzQ1NVoXDTEx\n" +
            "MDUzMTE3MzQ1NVowdDELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAk5ZMREwDwYDVQQHEwhOZXcgWW9y\n" +
            "azEXMBUGA1UEChMOQXRyaWNvcmUsIEluYy4xGjAYBgNVBAsTEUF0cmljb3JlIFNlcnZpY2VzMRAw\n" +
            "DgYDVQQDEwdKT1NTTyAyMIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2\n" +
            "EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7\n" +
            "ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUA\n" +
            "l2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdR\n" +
            "WVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx\n" +
            "+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAaJG9q75JoyuZ69DW\n" +
            "biJIr7ARf5W2H2AFwwmIe2ykdp8NOZbxE50WSwv+XnyHXNiBAl2HXKbxnglzK/liU3Cc+EyZJhxm\n" +
            "psEToKX0OZ7sgTbKQ+ftoku0/SBQks5KjFkZDDoylcJNYQewPBAoiHkxprLIy46UGHaEGema+TqB\n" +
            "hJcwCwYHKoZIzjgEAwUAAy8AMCwCFCF6xU7fTvuz4Yy0k87r5aT398V1AhRMB9iek8ZuDFGAO8BH\n" +
            "Ydh80QefCg==\n" +
            "-----END CERTIFICATE-----";
    
    private String licensePath = "etc/atricore.lic";

    private LicenseSigner signer;

    private Map<String, ProductFeature> productFeatures = new HashMap<String, ProductFeature>();

    public LicenseType activateLicense(byte[] license) throws InvalidLicenseException {
        LicenseType licenseType = null;
        try {            
            byte[] decoded = unzipAndDecodeLicense(license);
            //unmarshal
            licenseType = XmlUtils.unmarshalLicense(new ByteArrayInputStream(decoded), false);
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
            licenseType = XmlUtils.unmarshalLicense(new ByteArrayInputStream(decoded), false);
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
            LicenseType consoleLicense = getCurrentLicense();
            // 2. Validate
            validateLicense(consoleLicense);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InvalidLicenseException(e);
        }
    }

    public void validateFeature(String group, String name, String version, LicenseType license) throws InvalidFeatureException {
        try {

            Calendar now = Calendar.getInstance();
            LicenseType lic = license;

            boolean valid = false;

            for (LicensedFeatureType feature : lic.getLicensedFeature()) {

                for(FeatureType ft : feature.getFeature()) {

                    logger.info("Validatig License for Feature " +
                            ft.getGroup() + "/" +
                            ft.getName() + "/" +
                            ft.getVersion() + "[" +
                            (ft.getIssueInstant() != null ? ft.getIssueInstant().toString() : "<no-issue-instant>") +
                            ":" +
                            (ft.getExpiresOn() != null ? ft.getExpiresOn().toString() : "<perpetual>") +
                            "]");

                    if (ft.getGroup().equals(group) &&
                            ft.getName().equals(name)) {

                        // TODO : Check version range !
                        if (ft.getExpiresOn() != null && now.after(ft.getExpiresOn())) {

                            // TODO : Expired features do not fail the entired license (improve!)

                            String warn = "***************** FEATURE LICENE HAS " +
                                    ft.getGroup() + "/" +
                            ft.getName() + "/" +
                            ft.getVersion() + " HAS EXPIRED !!!";

                            logger.warn(warn);
                            logger.warn(warn);

                            System.out.println(warn);
                            System.out.println(warn);

                        }

                        valid = true;
                        break;
                    }
                }

            }

            if (!valid)
                throw new InvalidFeatureException(group + "/" + name + "/" + version);

        } catch (Exception e) {
            throw new InvalidFeatureException(e);
        }
    }

    public LicenseType getCurrentLicense() throws InvalidLicenseException {
        try {
            LicenseType consoleLicense = loadLicense();
            validateLicense(consoleLicense);
            return consoleLicense;
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

        Calendar now = Calendar.getInstance();

        logger.info("Validatig License " +
                license.getID() + "/" +
                license.getVersion() + "[" +
                (license.getIssueInstant() != null ? license.getIssueInstant().toString() : "<no-issue-instant>") +
                ":" +
                (license.getExpiresOn() != null ? license.getExpiresOn().toString() : "<perpetual>") +
                "]");

        
        if (license.getExpiresOn() != null && now.after(license.getExpiresOn())) {
            throw new InvalidLicenseException("Product License expired on " +
                    license.getExpiresOn().toString());
        }

        for (ProductFeature pf : productFeatures.values()) {
            try {
                validateFeature(pf.getGroup(), pf.getName(), pf.getVersion(), license);
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
            consoleLicense = XmlUtils.unmarshalLicense(new ByteArrayInputStream(licenseContent), false);

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
