package org.atricore.idbus.kernel.main.authn.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author: sgonzalez@atriocore.com
 * @date: 4/29/13
 */
public class PasswordUtil {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]?";

    private static final String OTHER_CHAR_PROP = "!@%&()_+-=[]?";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
    private static final String PASSWORD_ALLOW_BASE_SHUFFLE = shuffleString(PASSWORD_ALLOW_BASE);
    private static final String PASSWORD_ALLOW_B = PASSWORD_ALLOW_BASE_SHUFFLE;

    // Property friendly allowed chars
    private static final String PASSWORD_ALLOW_PROPS = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR_PROP;
    private static final String PASSWORD_ALLOW_PROPS_SHUFFLE = shuffleString(PASSWORD_ALLOW_PROPS);
    private static final String PASSWORD_ALLOW_P = PASSWORD_ALLOW_PROPS_SHUFFLE;


    private static SecureRandom random = new SecureRandom();


    private static final Log logger = LogFactory.getLog(PasswordUtil.class);


    public static String generateRandomPassword(int length) {
        return generateRandomPassword(length, PASSWORD_ALLOW_B);
    }

    public static String generateRandomPasswordForProp(int length) {
        return generateRandomPassword(length, PASSWORD_ALLOW_P);
    }


    public static String generateRandomPassword(int length, String allowed) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(allowed.length());
            char rndChar = allowed.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();

    }

    // shuffle
    public static String shuffleString(String string) {
        List<String> letters = Arrays.asList(string.split(""));
        Collections.shuffle(letters);
        String result = "";
        for (String l : letters) {
            result += l;
        }
        return result;
    }

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
            if (hashAlgorithm != null) {

                hash = digest.digest(passBytes);
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
            } else {
                // No hashing at this point, return password as is
                passwordHash = password;
            }


        } catch (Exception e) {
            logger.error("Password hash calculation failed : \n" + e.getMessage(), e);
        }

        return passwordHash;

    }
}
