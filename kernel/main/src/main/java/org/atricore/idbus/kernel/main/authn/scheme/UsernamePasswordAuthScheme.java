/*
 * Atricore IDBus
 *
 * Copyright (c) 2009, Atricore Inc.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.atricore.idbus.kernel.main.authn.scheme;


import at.favre.lib.crypto.bcrypt.BCrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.util.CipherUtil;
import org.atricore.idbus.kernel.main.authn.util.Crypt;
import org.atricore.idbus.kernel.main.authn.util.PasswordUtil;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;

/**
 * Basic authentication scheme, supporting username and password credentials.
 * <p/>
 * Configuration properties supported by this authenticator are :
 * <ul>
 * <li>hashAlgorithm: The message digest algorithm to be used when hashing passwords.
 * If not specified, no hashing is used.
 * This must be an algorithm supported by the java.security.MessageDigest class on your platform.
 * For J2SE 1.4.2 you can check :
 * <a href="http://java.sun.com/j2se/1.4.2/docs/guide/security/CryptoSpec.html#AppB">Java Cryptography Architecture API Specification & Reference - Apendix B : Algorithms</a>
 * </li>
 * <li>hashEncoding: The econding used to store hashed passwords.
 * Supported values are HEX, BASE64.</li>
 * <li>ignorePasswordCase: If true, password case will be igonred. This property is ignored if a hashAlgorithm was specified.
 * Default to false.</li>
 * <li>ignoreUserCase: If ture, username case will be ignored.</li>
 * <li>credential-store: The credential store configured for this authenticator.
 * Check specific stores for specific configuraiton options</li>
 * <li>credential-store-key-adapter: The credential store key adapter configured for this authenticator.
 * Check specific stores for specific configuraiton options</li>
 * </ul>
 * </p>
 * <p/>
 * Sample authenticator configuration for basic authentication (username/password) :
 * </p>
 * <pre>
 *         &lt;authentication-scheme&gt;
 * <p/>
 *         &lt;class&gt;org.josso.auth.scheme.UsernamePasswordAuthScheme&lt;/class&gt;
 *           &lt;hashAlgorithm&gt;MD5&lt;/hashAlgorithm&gt;
 *           &lt;hashEncoding&gt;HEX&lt;/hashEncoding&gt;
 *           &lt;ignorePasswordCase&gt;false&lt;/ignorePasswordCase&gt;
 *           &lt;ignoreUserCase&gt;false&lt;/ignoreUserCase&gt;
 * <p/>
 *           &lt;!-- Configure the proper store here --&gt;
 *           &lt;credential-store&gt;
 *           ...
 *           &lt;/credential-store&gt;
 * <p/>
 *           &lt;credential-store-key-adapter&gt;
 *           ...
 *           &lt;/credential-store-key-adapter&gt;
 * <p/>
 * 	 &lt;/authentication-scheme&gt;
 * <p/>
 * </pre>
 *
 * @org.apache.xbean.XBean element="basic-auth-scheme"
 *
 * @author <a href="mailto:sgonzalez@josso.org">Sebastian Gonzalez Oyuela</a>
 * @version $Id: UsernamePasswordAuthScheme.java 1040 2009-03-05 00:56:52Z gbrigand $
 * @see org.atricore.idbus.kernel.main.store.identity.CredentialStore
 * @see org.atricore.idbus.kernel.main.store.AbstractStore
 * @see UsernamePasswordCredentialProvider
 */

public class UsernamePasswordAuthScheme extends AbstractAuthenticationScheme {


    private static final Log logger = LogFactory.getLog(UsernamePasswordAuthScheme.class);

    private String _hashAlgorithm;

    private String _hashEncoding;

    private String _hashCharset;

    private boolean _ignorePasswordCase;

    private boolean _ignoreUserCase;

    // Some spetial configuration attributes,

    /**
     * This attribute is only used when CRYPT hashing is configured.  The default value is 2.
     */
    private int _saltLength = 2;

    private String _saltSuffix;

    private String _saltPrefix;

    protected Credential[] _knowCredentials;

    public UsernamePasswordAuthScheme() {
        this.setName("basic-authentication");
    }

    /**
     * The username received as UserNameCredential instance, if any.  Otherwise the UserIdCredential
     */
    public Principal getPrincipal() {

        String principalName = getUserName(_knowCredentials);
        if (principalName == null)
            principalName = getUserId(_knowCredentials);

        return new SimplePrincipal(principalName);
    }

    @Override
    public Principal getInputPrincipal() {
        String principalName = getUserName(_inputCredentials);
        if (principalName == null)
            principalName = getUserId(_inputCredentials);
        return new SimplePrincipal(principalName);
    }


    /**
     * The username recieved as UserIdCredential instance, if any.
     */
    public Principal getPrincipal(Credential[] credentials) {
        String username = getUserName(credentials);
        if (username == null) {
            username = getUserId(credentials);
            logger.debug("Using UserId as username " + username);
        } else {
            logger.debug("Using UserName as username " + username);
        }

        return new SimplePrincipal(username);
    }

    /**
     * Authenticates the user using recieved credentials to proof his identity.
     *
     * @return the Principal if credentials are valid, null otherwise.
     */
    public boolean authenticate() throws SSOAuthenticationException {

        setAuthenticated(false);

        String userid = getUserId(_inputCredentials);
        String username = getUserName(_inputCredentials);
        String password = getPassword(_inputCredentials);

        // Check if all credentials are present userid or username and password.
        if (((userid == null || userid.length() == 0) && (username == null || username.length() == 0))
                || password == null || password.length() == 0) {

            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Userid" + (userid == null || userid.length() == 0 ? " not" : "") + " provided. " +
                           "Username" + (username == null || username.length() == 0 ? " not" : "") + " provided. " +
                           "Password " + (password == null || password.length() == 0 ? " not" : "") + " provided.");
            }

            // We don't support empty values !
            return false;
        }

        _knowCredentials = getKnownCredentials();
        String knownUserId = getUserId(_knowCredentials);
        String knownUserName = getUserName(_knowCredentials);

        // Get user and known user
        String user = userid != null ? userid : username;
        String knownUser = knownUserId != null ? knownUserId : knownUserName;
        String expectedPassword = getPassword(_knowCredentials);

        // Validate user identity ...
        if (user != null) {
            if (!validateUser(user, knownUser)) {
                _policies.add(new InvalidUsernameAuthnPolicy(_knowCredentials));
                return false;
            }

        }

        // Do not validate password if username does not match
        if (!validatePassword(password, expectedPassword)) {
            _policies.add(new InvalidPasswordAuthnPolicy(_knowCredentials));
            return false;
        }


        if (logger.isDebugEnabled())
            logger.debug("[authenticate()], Principal authenticated [" + userid + "/" + knownUserName + "]");

        // We have successfully authenticated this user.
        setAuthenticated(true);
        return true;
    }

    /**
     * Only one password credential supported.
     */
    public Credential[] getPrivateCredentials() {

        Credential c = getPasswordCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;

    }

    /**
     * Only one username credential supported.
     */
    public Credential[] getPublicCredentials() {
        Credential c = getUserIdCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;
    }

    @Override
    public Credential newEncodedCredential(String name, Object value) {
        try {
            String v = (String) value;
            if (name.equals(UsernamePasswordCredentialProvider.PASSWORD_CREDENTIAL_NAME))
                v = createPasswordHash(v, getKnownCredentials());

            return super.newEncodedCredential(name, v);

        } catch (SSOAuthenticationException e) {
            logger.error("Cannot create encoded credential " + e.getMessage(), e);
            return null;
        }

    }

    // --------------------------------------------------------------------
    // Protected utils
    // --------------------------------------------------------------------

    /**
     * This method validates the input password agaist the expected password.
     *
     * @param inputPassword plain text password
     * @param storedHashedPassword
     */
    protected boolean validatePassword(String inputPassword, String storedHashedPassword) throws SSOAuthenticationException {

        if (logger.isDebugEnabled())
            logger.debug("Validating passwords...");

        String saltPrefix = getSalt(_knowCredentials);
        if (getSaltPrefix() != null) {
            if (saltPrefix == null)
                saltPrefix = getSaltPrefix();
            else
                saltPrefix  = getSaltPrefix() + saltPrefix;
        }

        return PasswordUtil.verifyPwd(inputPassword, storedHashedPassword, getSaltLength(), getSaltSuffix(), saltPrefix,
                getHashAlgorithm(), getHashEncoding(), getHashCharset(), getDigest());
    }

    /**
     * This method validates the input password agaist the expected password.
     *
     * @param inputUsername
     * @param expectedUsername
     */
    protected boolean validateUser(String inputUsername, String expectedUsername) {

        if (logger.isDebugEnabled())
            logger.debug("Validating usernames [" + inputUsername + "/" + expectedUsername + "]");

        if (inputUsername == null && expectedUsername == null)
            return false;

        if (_ignoreUserCase)
            return inputUsername.equalsIgnoreCase(expectedUsername);
        else
            return inputUsername.equals(expectedUsername);
    }


    /**
     * This method allows password hashing.
     * In order to work, you need to specify hashAlgorithm and hashEncoding properties.
     * You can optionally set hashCharset property.
     *
     * @return the hashed password.
     */
    protected String createPasswordHash(String password, Credential[] knowCredentials) throws SSOAuthenticationException {

        // If none of this properties are set, do nothing ...
        if (getHashAlgorithm() == null && getHashEncoding() == null) {
            // Nothing to do ...
            return password;
        }

        if (logger.isDebugEnabled())
            logger.debug("Creating password hash with algorithm/encoding [" + getHashAlgorithm() + "/" + getHashEncoding() + "]");

        String saltPrefix = getSalt(knowCredentials);
        if (getSaltPrefix() != null) {
            if (saltPrefix == null)
                saltPrefix = getSaltPrefix();
            else
                saltPrefix  = getSaltPrefix() + saltPrefix;
        }

        return PasswordUtil.createPasswordHash(password, getSaltLength(), getSaltSuffix(), saltPrefix,
                getHashAlgorithm(), getHashEncoding(), getHashCharset(), getDigest());

    }


    /**
     * Only invoke this if algorithm is set.
     *
     * @throws SSOAuthenticationException
     */
    protected MessageDigest getDigest() throws SSOAuthenticationException {

        if (_hashAlgorithm.equalsIgnoreCase("BCRYPT"))
            return null;

        if (_hashAlgorithm.equalsIgnoreCase("CRYPT"))
            return null;

        MessageDigest _digest = null;
        if (_hashAlgorithm != null) {

            try {
                _digest = MessageDigest.getInstance(_hashAlgorithm);
                logger.debug("Using hash algorithm/encoding : " + _hashAlgorithm + "/" + _hashEncoding);
            } catch (NoSuchAlgorithmException e) {
                logger.error("Algorithm not supported : " + _hashAlgorithm, e);
                throw new SSOAuthenticationException(e.getMessage(), e);
            }
        }

        return _digest;

    }

    /**
     * Gets the username from the received credentials.
     *
     * @param credentials
     */
    protected String getUserId(Credential[] credentials) {
        UserIdCredential c = getUserIdCredential(credentials);
        if (c == null)
            return null;

        return (String) c.getValue();
    }

    protected String getUserName(Credential[] credentials) {
        UserNameCredential c = getUserNameCredential(credentials);
        if (c == null)
            return null;
        return (String) c.getValue();
    }

    /**
     * Gets the password from the recevied credentials.
     *
     * @param credentials
     */
    protected String getPassword(Credential[] credentials) {
        PasswordCredential p = getPasswordCredential(credentials);
        if (p == null)
            return null;
        return (String) p.getValue();
    }

    protected String getSalt(Credential[] credentials) {
        SaltCredential s = getSaltCredential(credentials);
        if (s == null)
            return null;
        return (String) s.getValue();
    }

    /**
     * Gets the credential that represents a password.
     *
     * @param credentials
     */
    protected PasswordCredential getPasswordCredential(Credential[] credentials) {
        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i] instanceof PasswordCredential) {
                return (PasswordCredential) credentials[i];
            }
        }
        return null;
    }

    /**
     * Gets the credential that represents a password.
     *
     * @param credentials
     */
    protected SaltCredential getSaltCredential(Credential[] credentials) {
        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i] instanceof SaltCredential) {
                return (SaltCredential) credentials[i];
            }
        }
        return null;
    }


    /**
     * Gets the credential that represents a Username.
     */
    protected UserIdCredential getUserIdCredential(Credential[] credentials) {

        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i] instanceof UserIdCredential) {
                return (UserIdCredential) credentials[i];
            }
        }
        return null;
    }


    protected UserNameCredential getUserNameCredential(Credential[] credentials) {

        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i] instanceof UserNameCredential) {
                return (UserNameCredential) credentials[i];
            }
        }
        return null;
    }

    protected CredentialProvider doMakeCredentialProvider() {
        return new UsernamePasswordCredentialProvider();
    }

    public String getHashAlgorithm() {
        return _hashAlgorithm;
    }

    public void setHashAlgorithm(String hashAlgorithm) {
        if (hashAlgorithm != null && hashAlgorithm.equals(""))
            hashAlgorithm = null;
        _hashAlgorithm = hashAlgorithm;
    }

    /**
     * Getter for the encoding used for password hashing.
     * Supported values : HEX, BASE64
     */
    public String getHashEncoding() {
        return _hashEncoding;
    }

    /**
     * Setter for the encoding used for password hashing.
     * Supported values : HEX, BASE64
     */
    public void setHashEncoding(String hashEnconding) {
        if (hashEnconding != null && hashEnconding.equals(""))
            hashEnconding = null;
        _hashEncoding = hashEnconding;
    }

    public String getHashCharset() {
        return _hashCharset;
    }


    public void setHashCharset(String hashCharset) {
        _hashCharset = hashCharset;
    }

    public void setSaltLength(String saltLength) {
        setSaltLength(Integer.valueOf(saltLength).intValue());
    }

    /**
     * Only used when CRYPT is configured, default value is 2.
     */
    public int getSaltLength() {
        return _saltLength;
    }

    public void setSaltLength(int sl) {
        _saltLength = sl;
    }

    public String getSaltSuffix() {
        return _saltSuffix;
    }

    public void setSaltSuffix(String saltValue) {
        _saltSuffix = saltValue;
    }

    public String getSaltPrefix() {
        return _saltPrefix;
    }

    public void setSaltPrefix(String saltValue) {
        _saltPrefix = saltValue;
    }

    /**
     * Values : true , false,
     */
    public void setIgnorePasswordCase(String ignorePasswordCase) {
        _ignorePasswordCase = Boolean.valueOf(ignorePasswordCase).booleanValue();
    }

    /**
     * Values : true , false,
     */
    public void setIgnoreUserCase(String ignoreUserCase) {
        _ignoreUserCase = Boolean.valueOf(ignoreUserCase).booleanValue();
    }

    public Object clone() {

        UsernamePasswordAuthScheme s = (UsernamePasswordAuthScheme) super.clone();

        s.setHashAlgorithm(_hashAlgorithm);
        s.setHashCharset(_hashCharset);
        s.setHashEncoding(_hashEncoding);
        s.setIgnorePasswordCase(_ignorePasswordCase + "");
        s.setIgnoreUserCase(_ignoreUserCase + "");
        s.setName(_name);

        return s;
    }

}
