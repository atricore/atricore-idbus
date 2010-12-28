package com.atricore.liveservices.liveupdate._1_0.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class LiveUpdateKeystoreKeyResolver extends LiveUpdateKeyResolverImpl {

    private static final Log logger = LogFactory.getLog(LiveUpdateKeystoreKeyResolver.class);

    private Boolean initiated = false;
    private String keystoreType = "JKS";
    private byte[] keystoreFile;
    private String keystorePass;
    private String privateKeyAlias;
    private String privateKeyPass;
    private String certificateAlias;

    public void init() throws LiveUpdateKeyResolverException {
        InputStream is = null;
        try {
            KeyStore keystore = KeyStore.getInstance(keystoreType);

            if (keystoreFile == null)
                throw new IllegalStateException("No keystore defined!");

            // DO NOT CLOSE THE INPUT STREAM !
            is = new ByteArrayInputStream(keystoreFile);

            //load the keystore
            keystore.load(is, keystorePass.toCharArray());

            if (certificateAlias != null)
                certificate = keystore.getCertificate(certificateAlias);

            if (privateKeyAlias != null)
                privateKey = (PrivateKey) keystore.getKey(privateKeyAlias, privateKeyPass != null ? privateKeyPass.toCharArray() : null);

            initiated = true;
        } catch (Exception e) {
            throw new LiveUpdateKeyResolverException("Error accessing or reading keystore", e);
        }

        if (privateKeyAlias != null && privateKey == null)
            throw new LiveUpdateKeyResolverException("No private key found for : " + privateKeyAlias + " in " + keystoreFile);

        if (logger.isDebugEnabled())
            logger.debug("Found private key : " +
                " Format:" + privateKey.getFormat() +
                " Algorithm:" + privateKey.getAlgorithm() +
                " Class:" + privateKey.getClass().getName());

        if (certificateAlias != null && certificate == null)
            throw new LiveUpdateKeyResolverException("No certificate found for : " + certificateAlias + " in " + keystoreFile);

        if (logger.isDebugEnabled())
            logger.debug("Found certificate : " +
                 " Type:" + certificate.getType() +
                 " Class:" + certificate.getClass().getName());


    }

    @Override
    public Certificate getCertificate() throws LiveUpdateKeyResolverException {
        if (!initiated)
            init();

        return super.getCertificate();
    }

    @Override
    public PrivateKey getPrivateKey() throws LiveUpdateKeyResolverException {
        if (!initiated)
            init();

        return super.getPrivateKey();
    }

    public String getKeystoreType() {
        return keystoreType;
    }

    public void setKeystoreType(String keystoreType) {
        this.keystoreType = keystoreType;
    }

    public byte[] getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(byte[] keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public String getPrivateKeyPass() {
        return privateKeyPass;
    }

    public void setPrivateKeyPass(String privateKeyPass) {
        this.privateKeyPass = privateKeyPass;
    }

    public String getCertificateAlias() {
        return certificateAlias;
    }

    public void setCertificateAlias(String certificateAlias) {
        this.certificateAlias = certificateAlias;
    }
}
