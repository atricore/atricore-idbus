package com.atricore.josso2.licensing._1_0.util;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public class LicenseKeyResolverImpl implements LicenseKeyResolver {

    protected Certificate certificate;
    protected PrivateKey privateKey;

    protected LicenseKeyResolverImpl() {
    }

    public LicenseKeyResolverImpl(Certificate cert, PrivateKey key) {
        certificate = cert;
        privateKey = key;
    }

    public LicenseKeyResolverImpl(Certificate cert) {
        this( cert, null );
    }

    public LicenseKeyResolverImpl(PrivateKey key) {
        this( null, key );
    }

    public Certificate getCertificate() throws LicenseKeyResolverException {
        return certificate;
    }

    public PrivateKey getPrivateKey () throws LicenseKeyResolverException {
        return privateKey;
    }

    @Override
    public String toString() {
        return super.toString() + "[" +
                (certificate != null ? ",certificate.type=" + certificate.getType() : "") +
                (privateKey != null ? ",privateKey.format=" + privateKey.getFormat() : "") +
                (privateKey != null ? ",privateKey.algorithm=" + privateKey.getAlgorithm() : "" ) +
                "]";
    }
}