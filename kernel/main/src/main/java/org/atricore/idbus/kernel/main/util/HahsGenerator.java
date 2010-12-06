package org.atricore.idbus.kernel.main.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:sgonzalez@atricore.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id$
 */
public class HahsGenerator {

    public static String md5(String value)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return md5(value, "UTF-8");
    }

    public static String md5(String value, String encoding)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new String(md5(value.getBytes(encoding)), encoding);
    }


    public static byte[] md5(byte[] value) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] sha1hash = new byte[40];
        md.update(value);
        sha1hash = md.digest();

        return sha1hash;

    }

    public static String sha1(String value)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return sha1(value, "UTF-8");
    }

    public static String sha1(String value, String encoding)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new String(sha1(value.getBytes(encoding)), encoding);
    }

    public static byte[] sha1(byte[] value)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(value);
        sha1hash = md.digest();

        return sha1hash;

    }

}
