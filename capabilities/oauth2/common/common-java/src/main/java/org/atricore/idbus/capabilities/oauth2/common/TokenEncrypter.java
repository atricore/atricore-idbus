package org.atricore.idbus.capabilities.oauth2.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface TokenEncrypter {

    String encrypt(String tokenValue) throws OAuth2EncryptionException;

    String decrypt(String encryptedTokenValue) throws OAuth2EncryptionException;

    String getEncryptAlg();
}
