package com.atricore.idbus.console.services.impl;

import com.atricore.idbus.console.lifecycle.main.exception.SignOnException;
import com.atricore.idbus.console.services.dto.UserDTO;
import com.atricore.idbus.console.services.spi.SignOnAjaxService;
import com.atricore.idbus.console.services.spi.UserProvisioningAjaxService;
import com.atricore.idbus.console.services.spi.request.FindUserByUsernameRequest;
import com.atricore.idbus.console.services.spi.request.SignOnRequest;
import com.atricore.idbus.console.services.spi.request.SignOutRequest;
import com.atricore.idbus.console.services.spi.response.FindUserByUsernameResponse;
import com.atricore.idbus.console.services.spi.response.SignOnResponse;
import com.atricore.idbus.console.services.spi.response.SignOutResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.provisioning.exception.ProvisioningException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: Dusan Fisic
 */
public class SignOnAjaxServiceImpl implements SignOnAjaxService {

    private static Log logger = LogFactory.getLog(SignOnAjaxServiceImpl.class);

    private UserProvisioningAjaxService usrProvService;

    private String hashEncoding  = "HEX";

    private String hashAlgorithm = "MD5";

    private String hashCharset = null;

    public SignOnResponse signOn(SignOnRequest signOnRequest) throws SignOnException {
        FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
        userRequest.setUsername(signOnRequest.getUsername());

        try{
            SignOnResponse response = new SignOnResponse();
            FindUserByUsernameResponse resp = usrProvService.findUserByUsername(userRequest);
            UserDTO retUser = resp.getUser();
            if (retUser != null &&
                    retUser.getUserPassword().equals(createPasswordHash(signOnRequest.getPassword()))) {
                response.setAuthenticatedUser(retUser);
            }
            if (retUser == null && logger.isTraceEnabled())
                 logger.trace("Unknown user with username: " + signOnRequest.getUsername() );

            return response;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SignOnException("Error finding user with username: " + userRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public SignOutResponse signOut(SignOutRequest signOutRequest) throws SignOnException {
        FindUserByUsernameRequest userRequest = new FindUserByUsernameRequest();
        try{
            SignOutResponse response = new SignOutResponse();
           
            return response;
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SignOnException("Error finding user with username: " + userRequest.getUsername() + " : " + e.getMessage(), e);
        }
    }

    public UserProvisioningAjaxService getUsrProvService() {
        return usrProvService;
    }

    public void setUsrProvService(UserProvisioningAjaxService usrProvService) {
        this.usrProvService = usrProvService;
    }

    protected String createPasswordHash(String password) throws ProvisioningException {

        // If none of this properties are set, do nothing ...
        if (getHashAlgorithm() == null && getHashEncoding() == null) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash for [" + password + "] with algorithm/encoding [" + getHashAlgorithm() + "/" + getHashEncoding() + "]");

        // Check for spetial encryption mechanisms, not supported by the JDK
        /* TODO
        if ("CRYPT".equalsIgnoreCase(getHashAlgorithm())) {
            // Get known password
            String knownPassword = getPassword(getKnownCredentials());
            String salt = knownPassword != null && knownPassword.length() > 1 ? knownPassword.substring(0, saltLenght) : "";

            return Crypt.crypt(salt, password);

        } */

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
                hash = getDigest().digest(passBytes);
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

    /**
     * Only invoke this if algorithm is set.
     *
     * @throws ProvisioningException
     */
    protected MessageDigest getDigest() throws ProvisioningException {

        MessageDigest digest = null;
        if (hashAlgorithm != null) {

            try {
                digest = MessageDigest.getInstance(hashAlgorithm);
                logger.debug("Using hash algorithm/encoding : " + hashAlgorithm + "/" + hashEncoding);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not supported : " + hashAlgorithm, e);
                throw new ProvisioningException(e.getMessage(), e);
            }
        }

        return digest;

    }

    public String getHashEncoding() {
        return hashEncoding;
    }

    public void setHashEncoding(String hashEncoding) {
        this.hashEncoding = hashEncoding;
    }

    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public String getHashCharset() {
        return hashCharset;
    }

    public void setHashCharset(String hashCharset) {
        this.hashCharset = hashCharset;
    }
}
