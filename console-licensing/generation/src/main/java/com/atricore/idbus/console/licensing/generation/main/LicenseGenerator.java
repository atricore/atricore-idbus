package com.atricore.idbus.console.licensing.generation.main;

import com.atricore.idbus.console.licensing.generation.LicenseGenerationException;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import com.atricore.josso2.licensing._1_0.util.LicenseKeystoreKeyResolver;
import com.atricore.josso2.licensing._1_0.util.LicenseSignatureException;
import com.atricore.josso2.licensing._1_0.util.LicenseSigner;
import com.atricore.josso2.licensing._1_0.util.XmlUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.apache.commons.codec.binary.Base64;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseGenerator {

    static Log logger = LogFactory.getLog(LicenseGenerator.class);

    private LicenseSigner signer;

    public LicenseType generate(String inLicense, String outLicense, String keystoreFile, String keystorePass, String keyName, String keyPass, String certAlias) throws LicenseGenerationException {
        ZipArchiveOutputStream zipOut = null;
        try {
            LicenseKeystoreKeyResolver keyResolver = new LicenseKeystoreKeyResolver();
            keyResolver.setKeystoreType("JKS");
            keyResolver.setKeystorePass(keystorePass);
            keyResolver.setPrivateKeyAlias(keyName);
            keyResolver.setPrivateKeyPass(keyPass);
            keyResolver.setCertificateAlias(certAlias);
            File file = new File(keystoreFile);
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[(int) file.length()];
            fis.read(b);
            keyResolver.setKeystoreFile(b);

            LicenseType unsigned = XmlUtils.unmarshallLicense(new FileInputStream(inLicense), false);
            LicenseType signed = signer.sign(unsigned, keyResolver);

            String licenseString = XmlUtils.marshalLicense(signed, false);

            //base64 encoding
            byte[] licenseEncoded = Base64.encodeBase64(licenseString.getBytes());

            //save license as zipped file
            FileOutputStream myOutputStream = new FileOutputStream(outLicense);
            ZipArchiveEntry entry = new ZipArchiveEntry("license");
            entry.setSize(licenseEncoded.length);
            
            zipOut = new ZipArchiveOutputStream(myOutputStream);

            zipOut.putArchiveEntry(entry);
            zipOut.write(licenseEncoded);
            zipOut.closeArchiveEntry();
            zipOut.finish();
            return signed;

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
        } catch (LicenseSignatureException e) {
            throw new LicenseGenerationException(e);
        } catch (Exception e) {
            throw new LicenseGenerationException(e);
        } finally {
            if (zipOut != null) {
                try {
                    zipOut.close();
                } catch (IOException e) { /* swallow */ }
            }
        }

    }

    LicenseType generate(LicenseType unsigned) {
        return null;
    }

    public void setSigner(LicenseSigner signer) {
        this.signer = signer;
    }    
}
