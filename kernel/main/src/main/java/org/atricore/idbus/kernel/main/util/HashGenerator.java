package org.atricore.idbus.kernel.main.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class HashGenerator {

    private static final int BASE64 = 0;

    private static final int HEX = 2;

    public static String md5(String value)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return md5(value, BASE64, "UTF-8");
    }

    public static String md5(String value, Integer encoding, String charset)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {

        byte[] md5 = md5(value.getBytes(charset));
        if (encoding != null)
            encode(md5, encoding);
        return new String(md5, charset);
    }

    public static byte[] md5(byte[] value) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash;
        md.update(value);
        md5hash = md.digest();

        return md5hash;
    }

    public static String sha1(String value)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return sha1(value, BASE64, "UTF-8");
    }

    public static String sha1(String value, Integer encoding, String charset)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {

        byte[] sha1 = sha1(value.getBytes(charset));
        if (encoding != null)
            encode(sha1, encoding);
        return new String(sha1, charset);
    }

    public static byte[] sha1(byte[] value)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;
        md.update(value);
        sha1hash = md.digest();


        return sha1hash;

    }

    public static String encode(byte[] value, int type) throws UnsupportedEncodingException {
        switch (type) {
             case BASE64:
                 return new String(Base64.encodeBase64(value));
             case HEX:
                 return new String(Hex.encodeHex(value));
             default:
                 throw new UnsupportedEncodingException(type+"");
        }

    }

}
