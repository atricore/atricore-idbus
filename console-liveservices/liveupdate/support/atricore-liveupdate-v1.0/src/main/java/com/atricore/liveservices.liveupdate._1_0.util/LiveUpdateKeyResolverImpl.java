package com.atricore.liveservices.liveupdate._1_0.util;

import java.security.PrivateKey;
import java.security.cert.Certificate;

public class LiveUpdateKeyResolverImpl implements LiveUpdateKeyResolver {

    protected Certificate certificate;
    protected PrivateKey privateKey;

    protected LiveUpdateKeyResolverImpl() {
    }

    public LiveUpdateKeyResolverImpl(Certificate cert, PrivateKey key) {
        certificate = cert;
        privateKey = key;
    }

    public LiveUpdateKeyResolverImpl(Certificate cert) {
        this( cert, null );
    }

    public LiveUpdateKeyResolverImpl(PrivateKey key) {
        this( null, key );
    }

    public Certificate getCertificate() throws LiveUpdateKeyResolverException {
        return certificate;
    }

    public PrivateKey getPrivateKey () throws LiveUpdateKeyResolverException {
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
