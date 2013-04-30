package org.atricore.idbus.capabilities.sso.ui.page.selfsvcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;

import java.security.MessageDigest;

/**
 * Replace this with something on the IdP side
 * 
 * @author: sgonzalez@atriocore.com
 * @date: 4/29/13
 */
public class PasswordUtil {
    
    private static final Log logger = LogFactory.getLog(PasswordUtil.class);

    public static String createPasswordHash(String password, String hashAlgorithm, String hashEncoding, MessageDigest digest) {

        // If none of this properties are set, do nothing ...
        if (hashAlgorithm == null && hashEncoding == null) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash for [" + password + "] with algorithm/encoding [" + hashAlgorithm + "/" + hashEncoding + "]");

        // Check for spetial encryption mechanisms, not supported by the JDK
        /* TODO
        if ("CRYPT".equalsIgnoreCase(hashAlgorithm)) {
            // Get known password
            String knownPassword = getPassword(getKnownCredentials());
            String salt = knownPassword != null && knownPassword.length() > 1 ? knownPassword.substring(0, saltLenght) : "";

            return Crypt.crypt(salt, password);

        } */

        byte[] passBytes;
        String passwordHash = null;

        // convert password to byte data
        passBytes = password.getBytes();

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
            logger.error("Password hash calculation failed : \n" + e.getMessage() != null ? e.getMessage() : e.toString(), e);
        }

        return passwordHash;

    }
}
