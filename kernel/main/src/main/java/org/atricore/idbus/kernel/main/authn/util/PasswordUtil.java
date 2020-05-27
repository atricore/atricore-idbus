package org.atricore.idbus.kernel.main.authn.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.sun.istack.Nullable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/29/13
 */
public class PasswordUtil {

    private static final Log logger = LogFactory.getLog(PasswordUtil.class);

    /**
     *
     * @param password
     * @param hashvalue
     * @param saltLenght Only used for CRYPT algorithm
     * @param saltSuffix Salt suffix for the password
     * @param saltPrefix Salt prefix for the password
     * @param hashAlgorithm
     * @param hashEncoding
     * @param hashCharset
     * @param digest
     * @return
     */
    public static boolean verifyPwd(String password, String hashvalue, int saltLenght, String saltSuffix, String saltPrefix,
                                    String hashAlgorithm, String hashEncoding, String hashCharset, MessageDigest digest) {

        if ("BCRYPT".equalsIgnoreCase(hashAlgorithm)) {
            BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hashvalue);
            return result.verified;
        }

        String expectedHash = createPasswordHash(password, saltLenght, saltSuffix, saltPrefix, hashAlgorithm, hashEncoding, hashCharset, digest);
        return hashvalue.equals(expectedHash);

    }

    public static boolean verifyPwd(String password, String hashvalue, String hashAlgorithm, String hashEncoding, MessageDigest digest) {
        return verifyPwd(password, hashvalue, 0,null, null, hashAlgorithm, hashEncoding, null, digest);

    }

    /**
     * Calculates a password with no salt
     *
     * @param password
     * @param hashAlgorithm
     * @param hashEncoding
     * @param digest
     * @return
     */
    public static String createPasswordHash(String password, String hashAlgorithm, String hashEncoding, MessageDigest digest) {
        return createPasswordHash(password, 0,null, null, hashAlgorithm, hashEncoding, null, digest);

    }

    /**
     *
     * @param password
     * @param saltSuffix Salt value used as suffix
     * @param saltPrefix Salt value used as prefix
     *
     * @param hashAlgorithm
     * @param hashEncoding
     * @param hashCharset
     * @param digest
     * @return
     */
    public static String createPasswordHash(String password, int saltLenght, String saltSuffix, String saltPrefix,
                                            String hashAlgorithm, String hashEncoding, String hashCharset, MessageDigest digest) {

        // Check for special encryption mechanisms, not supported by the JDK

        // Check for special encryption mechanisms, not supported by the JDK
        if ("CRYPT".equalsIgnoreCase(hashAlgorithm)) {
            // Get known password
            String cryptSalt = password != null && password.length() > 1 ? password.substring(0, saltLenght) : "";

            return Crypt.crypt(cryptSalt, password);

        }

        if (hashAlgorithm != null  && "BCRYPT".equalsIgnoreCase(hashAlgorithm)) {
            // Set the cost to a fixed 12 for now.
            return BCrypt.withDefaults().hashToString(12, password.toCharArray());
        }


        // Look for fixed salts:
        if (saltSuffix != null) {
            if (logger.isTraceEnabled())
                logger.trace("Using salt value as suffix");
            password = password + saltSuffix;
        }

        if (saltPrefix != null) {

            if (logger.isTraceEnabled())
                logger.trace("Using salt value as prefix");
            password = saltPrefix + password;
        }

        byte[] passBytes;
        String passwordHash = null;

        // convert password to byte data
        try {
            if (hashCharset == null)
                passBytes = password.getBytes();
            else
                passBytes = password.getBytes(hashCharset);
        } catch (UnsupportedEncodingException e) {
            logger.error("charset " + hashCharset + " not found. Using platform default.");
            passBytes = password.getBytes();
        }

        // calculate the hash and apply the encoding.
        try {

            byte[] hash;
            // Hash algorithm is optional
            if (hashAlgorithm != null)
                hash = digest.digest(passBytes);
            else
                hash = passBytes;

            // At this point, hashEncoding is required.
            if ("BASE64".equalsIgnoreCase(hashEncoding)) {
                passwordHash = CipherUtil.encodeBase64(hash);

            } else if ("HEX".equalsIgnoreCase(hashEncoding)) {
                passwordHash = CipherUtil.encodeBase16(hash);

            } else if (hashEncoding == null) {
                logger.error("You must specify a hashEncoding when using hashAlgorithm");

            } else {
                logger.error("Unsupported hash encoding format " + hashEncoding);

            }

        } catch (Exception e) {
            logger.error("Password hash calculation failed : \n" + e.getMessage(), e);
        }

        return passwordHash;

    }
}
