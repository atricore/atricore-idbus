package org.atricore.idbus.capabilities.openidconnect.main.op;

import com.nimbusds.oauth2.sdk.client.ClientInformation;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by sgonzalez on 8/7/15.
 */
public class KeyUtils {

    public static SecretKey getKey(ClientInformation clientInfo) throws NoSuchAlgorithmException {

        byte[] key = clientInfo.getSecret().getValueBytes();
        if (key.length != 32) {
            // We need a 32 byte length key, so  ...
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 32);
        }

        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

        return secretKey;
    }

}
