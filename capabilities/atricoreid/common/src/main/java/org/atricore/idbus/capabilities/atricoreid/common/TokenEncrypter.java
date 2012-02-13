package org.atricore.idbus.capabilities.atricoreid.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public interface TokenEncrypter {

    String encrypt(String tokenValue) throws AtricoreIDEncryptionException;

    String decrypt(String encryptedTokenValue) throws AtricoreIDEncryptionException;

    String getEncryptAlg();
}
