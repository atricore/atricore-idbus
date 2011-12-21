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

        // HMAC Signer
        HMACTokenSigner signer = new HMACTokenSigner();
        signer.setKey(config.getProperty("org.atricore.idbus.capabilities.oauth2.signKey", defaultKey));
        r.setTokenSigner(signer);

        // AES Encrypter
        AESTokenEncrypter encrypter = new AESTokenEncrypter();
        String encKey = config.getProperty("org.atricore.idbus.capabilities.oauth2.encryptKey", defaultKey);
        String base64Key = new String(Base64.encodeBase64(encKey.getBytes()));
        encrypter.setBase64key(base64Key);
        r.setTokenEncrypter(encrypter);

        return r;

    }
}
