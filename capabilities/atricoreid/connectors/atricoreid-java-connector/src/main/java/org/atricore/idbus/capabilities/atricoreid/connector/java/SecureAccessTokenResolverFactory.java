package org.atricore.idbus.capabilities.atricoreid.connector.java;

import org.apache.commons.codec.binary.Base64;
import org.atricore.idbus.capabilities.atricoreid.common.AESTokenEncrypter;
import org.atricore.idbus.capabilities.atricoreid.common.HMACTokenSigner;

/**
 * Secure resolver factory, for now it fixes HMAC-SHA1 and AES for signing and encrypting
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SecureAccessTokenResolverFactory extends AccessTokenResolverFactory {

    public static final String SHARED_SECRECT_PROPERTY = "org.atricore.idbus.capabilities.atricoreid.key";

    public static final String SHARED_SECRECT_SIGN_PROPERTY = "org.atricore.idbus.capabilities.atricoreid.signKey";

    public static final String SHARED_SECRECT_ENC_PROPERTY = "org.atricore.idbus.capabilities.atricoreid.encryptKey";

    public static final String TOKEN_VALIDITY_INTERVAL_PROPERTY = "org.atricore.idbus.capabilities.atricoreid.accessTokenValidityInterval";

    public AccessTokenResolver doMakeResolver() {

        // Resolver
        SecureAccessTokenResolverImpl r = new SecureAccessTokenResolverImpl();
        String defaultKey = config.getProperty(SHARED_SECRECT_PROPERTY);
        String encKey = config.getProperty(SHARED_SECRECT_ENC_PROPERTY, defaultKey);
        String signKey = config.getProperty(SHARED_SECRECT_SIGN_PROPERTY, defaultKey);
        long tkValidityInterval = Long.parseLong(config.getProperty(TOKEN_VALIDITY_INTERVAL_PROPERTY, "0"));

        // HMAC Signer
        HMACTokenSigner signer = new HMACTokenSigner();
        signer.setKey(signKey);
        r.setTokenSigner(signer);

        // AES Encrypter
        AESTokenEncrypter encrypter = new AESTokenEncrypter();
        encrypter.setBase64key(encKey);
        r.setTokenEncrypter(encrypter);

        r.setTokenValidityInterval(tkValidityInterval);

        return r;

    }
}
