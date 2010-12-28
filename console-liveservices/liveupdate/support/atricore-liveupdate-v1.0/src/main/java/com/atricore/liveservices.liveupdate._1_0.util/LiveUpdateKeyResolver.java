package com.atricore.liveservices.liveupdate._1_0.util;

import java.security.Key;
import java.security.cert.Certificate;

public interface LiveUpdateKeyResolver {

    Certificate getCertificate() throws LiveUpdateKeyResolverException;

    Key getPrivateKey() throws LiveUpdateKeyResolverException;
}
