package org.atricore.idbus.capabilities.atricoreid.common;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class HMACTokenSigner implements TokenSigner {

    private String key;

    private String signAlg = "HmacSHA1";

    public String signToken(String tokenValue) throws AtricoreIDSignatureException {

        try {

            Mac mac = Mac.getInstance(signAlg);
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            byte[] digest = mac.doFinal(tokenValue.getBytes());

            byte[] signature = new Base64().encode(digest);
            return new String(signature);

        } catch (NoSuchAlgorithmException e) {
            throw new AtricoreIDSignatureException(e);
        } catch (InvalidKeyException e) {
            throw new AtricoreIDSignatureException(e);
        }
    }

    public boolean isValid(String tokenValue, String tokenSignature) throws AtricoreIDSignatureException {
        return tokenSignature.equals(signToken(tokenValue));
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSignAlg() {
        return signAlg;
    }

    public void setSignAlg(String signAlg) {
        this.signAlg = signAlg;
    }
}
