package org.atricore.idbus.capabilities.oauth2.rserver;

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

        // Signer
        HMACTokenSigner signer = new HMACTokenSigner();
        signer.setKey(config.getProperty("org.atricore.idbus.capabilities.oauth2.signKey"));
        r.setTokenSigner(signer);

        // Encrypter
        AESTokenEncrypter encrypter = new AESTokenEncrypter();
        encrypter.setBase64key(config.getProperty("org.atricore.idbus.capabilities.oauth2.encryptKey"));
        r.setTokenEncrypter(encrypter);

        return r;

    }
}
