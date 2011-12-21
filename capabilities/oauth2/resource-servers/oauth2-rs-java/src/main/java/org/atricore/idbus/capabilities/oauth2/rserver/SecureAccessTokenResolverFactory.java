package org.atricore.idbus.capabilities.oauth2.rserver;

import org.apache.commons.codec.binary.Base64;
import org.atricore.idbus.capabilities.oauth2.common.AESTokenEncrypter;
import org.atricore.idbus.capabilities.oauth2.common.HMACTokenSigner;

/**
 * Secure resolver factory, for now it fixes HMAC-SHA1 and AES for signing and encrypting
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SecureAccessTokenResolverFactory extends AccessTokenResolverFactory {

    public AccessTokenResolver doMakeResolver() {

        // Resolver
        SecureAccessTokenResolverImpl r = new SecureAccessTokenResolverImpl();
        String defaultKey = config.getProperty("org.atricore.idbus.capabilities.oauth2.key");
        String encKey = config.getProperty("org.atricore.idbus.capabilities.oauth2.encryptKey", defaultKey);
        String signKey = config.getProperty("org.atricore.idbus.capabilities.oauth2.signKey", defaultKey);

        // HMAC Signer
        HMACTokenSigner signer = new HMACTokenSigner();
        signer.setKey(signKey);
        r.setTokenSigner(signer);

        // AES Encrypter
        AESTokenEncrypter encrypter = new AESTokenEncrypter();
        encrypter.setBase64key(encKey);
        r.setTokenEncrypter(encrypter);

        return r;

    }
}
