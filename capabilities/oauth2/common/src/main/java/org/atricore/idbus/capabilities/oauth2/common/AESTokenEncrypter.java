package org.atricore.idbus.capabilities.oauth2.common;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AESTokenEncrypter implements  TokenEncrypter {

    private String base64key;

    private String encryptAlg = "AES";

    public String encrypt(String tokenValue) throws OAuth2EncryptionException {
        try {
            return encryptAES(tokenValue, base64key);
        } catch (Exception e) {
            throw new OAuth2EncryptionException(e);
        }
    }

    public String decrypt(String encryptedTokenValue) throws OAuth2EncryptionException {
        try {
            return decryptAES(encryptedTokenValue, base64key);
        } catch (Exception e) {
            throw new OAuth2EncryptionException(e);
        }
    }

    /**
     * This generates a 128 AES key.
     *
     * @throws java.security.NoSuchAlgorithmException
     */
    public static SecretKeySpec generateAESKey() throws NoSuchAlgorithmException {

        SecretKeySpec skeySpec;

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey skey = kgen.generateKey();
        byte[] key = skey.getEncoded();

        skeySpec = new SecretKeySpec(key, "AES");

        return skeySpec;
    }

    /**
     * Creates an ecnrypted string using AES of the given message.  The string is encoded using base 64.
     */
    protected String encryptAES(String msg, String base64Key) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] key = decodeBase64 (base64Key);
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] text = cipher.doFinal(msg.getBytes());

        return encodeBase64(text);
    }

    /**
     * Decrypts the given text using AES
     */
    protected String decryptAES(String base64text, String base64Key) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        byte[] key = decodeBase64 (base64Key);
        SecretKeySpec skeySpec = new SecretKeySpec(key, encryptAlg);
        Cipher cipher = Cipher.getInstance(encryptAlg);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] text = decodeBase64(base64text);
        byte[] msg = cipher.doFinal(text);

        return new String (msg);

    }


    /**
     * Base64 encoding.  Charset ISO-8859-1 is assumed.
     */
    public static String encodeBase64(byte[] bytes) throws UnsupportedEncodingException {
        byte[] enc = Base64.encodeBase64(bytes);
        return new String(enc);
    }

    /**
     * Base64 encoding.  Charset ISO-8859-1 is assumed.
     */
    public static byte[] decodeBase64(String text) throws UnsupportedEncodingException {
        byte[] bin = Base64.decodeBase64(text.getBytes());
        return bin;
    }


    /**
     * Base16 encoding (HEX).
     */
    public static String encodeBase16(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            // top 4 bits
            char c = (char) ((b >> 4) & 0xf);
            if (c > 9)
                c = (char) ((c - 10) + 'a');
            else
                c = (char) (c + '0');
            sb.append(c);
            // bottom 4 bits
            c = (char) (b & 0xf);
            if (c > 9)
                c = (char) ((c - 10) + 'a');
            else
                c = (char) (c + '0');
            sb.append(c);
        }
        return sb.toString();
    }

    public String getBase64key() {
        return base64key;
    }

    public void setBase64key(String base64key) {
        this.base64key = base64key;
    }

    public String getEncryptAlg() {
        return encryptAlg;
    }

    public void setEncryptAlg(String encryptAlg) {
        this.encryptAlg = encryptAlg;
    }


}
