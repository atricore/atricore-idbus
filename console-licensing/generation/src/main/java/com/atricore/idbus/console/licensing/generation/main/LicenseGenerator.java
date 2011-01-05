package com.atricore.idbus.console.licensing.generation.main;

import com.atricore.idbus.console.licensing.generation.LicenseGenerationException;
import com.atricore.josso2.licensing._1_0.license.LicenseType;
import com.atricore.josso2.licensing._1_0.util.LicenseKeystoreKeyResolver;
import com.atricore.josso2.licensing._1_0.util.LicenseSignatureException;
import com.atricore.josso2.licensing._1_0.util.LicenseSigner;
import com.atricore.josso2.licensing._1_0.util.XmlUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseGenerator {

    static Log logger = LogFactory.getLog(LicenseGenerator.class);

    private LicenseSigner signer;

    public LicenseType generate(String inLicense, String outLicense, String keystoreFile, String keystorePass, String keyName, String keyPass, String certAlias) throws LicenseGenerationException {
        LicenseType consoleLicense = null;
        PrivateKey privateKey = null;
        Certificate certificate = null;
        Writer out = null;
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

            //saves the signed license as new file
            out = new OutputStreamWriter(new FileOutputStream(outLicense));
            out.write(licenseString);
            out.close();
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
        }

    }

    LicenseType generate(LicenseType unsigned) {
        return null;
    }

    public void setSigner(LicenseSigner signer) {
        this.signer = signer;
    }    
}
