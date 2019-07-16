package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.jose.SecretKeyDerivation;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by sgonzalez on 8/7/15.
 */
public class KeyUtils {

    /**
     * Creats an AES Secret from client MD secret.
     *
     * The algorithm requires the key to be of the same length as the
     * "block-size" of the hashing algorithm (SHA256 = 64-byte blocks).
     *
     * Extension is performed by appending zeros.
     *
     * This implementation creates a 256 bit key.
     *
     */
    public static SecretKey extendOrTruncateKey(ClientInformation clientInfo) throws NoSuchAlgorithmException, JOSEException {
        // Client private key.
        return SecretKeyDerivation.deriveSecretKey(clientInfo.getSecret(), 256);
    }

    /**
     * Creats an AES Secret from client MD secret.
     *
     * The algorithm requires the key to be of the same length as the
     * "block-size" of the hashing algorithm (SHA256 = 64-byte blocks).
     *
     * Extension is performed by appending zeros.
     *
     * You can provide the length in bits: 128, 192, 256, 384, 512)
     *
     */
    public static SecretKey extendOrTruncateKey(ClientInformation clientInfo, int bit) throws NoSuchAlgorithmException, JOSEException {
        // Client private key.
        return SecretKeyDerivation.deriveSecretKey(clientInfo.getSecret(), bit);
    }

}
