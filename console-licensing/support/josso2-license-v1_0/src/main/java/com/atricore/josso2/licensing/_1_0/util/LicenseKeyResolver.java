package com.atricore.josso2.licensing._1_0.util;

import java.security.Key;
import java.security.cert.Certificate;

public interface LicenseKeyResolver {

    Certificate getCertificate() throws LicenseKeyResolverException;

    Key getPrivateKey() throws LicenseKeyResolverException;
}