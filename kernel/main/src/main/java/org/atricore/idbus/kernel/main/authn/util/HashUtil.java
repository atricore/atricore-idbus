package org.atricore.idbus.kernel.main.authn.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class HashUtil {

    private static Log logger = LogFactory.getLog(HashUtil.class);

    private String _hashAlgorithm;

    private String _hashEncoding;

    private String _hashCharset;

    private int _saltLength = 2;

    /**
     * This method allows password hashing.
     * In order to work, you need to specify hashAlgorithm and hashEncoding properties.
     * You can optionally set hashCharset property.
     *
     * @param password password
     * @return the hashed password.
     */
    public String createPasswordHash(String password) {
        // If none of this properties are set, do nothing ...
        if (password == null || (_hashAlgorithm == null && _hashEncoding == null)) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash for [" + password + "] with algorithm/encoding [" + _hashAlgorithm + "/" + _hashEncoding + "]");

        // Check for spatial encryption mechanisms, not supported by the JDK
        if ("CRYPT".equalsIgnoreCase(_hashAlgorithm)) {
            String salt = password.substring(0, _saltLength);
            return Crypt.crypt(salt, password);
        }

        byte[] passBytes;
        String passwordHash = null;

        // convert password to byte data
        try {
            if (_hashCharset == null)
                passBytes = password.getBytes();
            else
                passBytes = password.getBytes(_hashCharset);
        } catch (UnsupportedEncodingException e) {
            logger.error("charset " + _hashCharset + " not found. Using platform default.");
            passBytes = password.getBytes();
        }

        // calculate the hash and apply the encoding.
        try {

            byte[] hash;
            // Hash algorithm is optional
            if (_hashAlgorithm != null)
                hash = getDigest().digest(passBytes);
            else
                hash = passBytes;

            // At this point, hashEncoding is required.
            if ("BASE64".equalsIgnoreCase(_hashEncoding)) {
                passwordHash = CipherUtil.encodeBase64(hash);

            } else if ("HEX".equalsIgnoreCase(_hashEncoding)) {
                passwordHash = CipherUtil.encodeBase16(hash);

            } else if (_hashEncoding == null) {
                logger.error("You must specify a hashEncoding when using hashAlgorithm");

            } else {
                logger.error("Unsupported hash encoding format " + _hashEncoding);

            }

        } catch (Exception e) {
            logger.error("Password hash calculation failed : \n" + e.getMessage() != null ? e.getMessage() : e.toString(), e);
        }

        return passwordHash;
    }

    /**
     * Only invoke this if algorithm is set.
     */
    protected MessageDigest getDigest() {

        MessageDigest _digest = null;
        if (_hashAlgorithm != null) {

            try {
                _digest = MessageDigest.getInstance(_hashAlgorithm);
                logger.debug("Using hash algorithm/encoding : " + _hashAlgorithm + "/" + _hashEncoding);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not supported : " + _hashAlgorithm, e);
            }
        }

        return _digest;

    }

    public void setHashAlgorithm(String _hashAlgorithm) {
        this._hashAlgorithm = _hashAlgorithm;
    }

    public void setHashEncoding(String _hashEncoding) {
        this._hashEncoding = _hashEncoding;
    }

    public void setHashCharset(String _hashCharset) {
        this._hashCharset = _hashCharset;
    }

    public void setSaltLength(int _saltLength) {
        this._saltLength = _saltLength;
    }
}
