package org.atricore.idbus.capabilities.oath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticationFailure;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialKey;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.authn.scheme.UserIdCredential;
import org.atricore.idbus.kernel.main.authn.scheme.UserNameCredential;
import org.atricore.idbus.kernel.main.authn.util.UserUtil;
import org.atricore.idbus.kernel.main.provisioning.domain.User;
import org.atricore.idbus.kernel.main.store.SimpleUserKey;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.CredentialStore;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.atricore.idbus.capabilities.sts.main.authenticators.TwoFactorSecurityTokenAuthenticator.PASSCODE_NS;

public class TOTPAuthenticator implements SecurityTokenAuthenticator, CredentialProvider {


    private static final Log logger = LogFactory.getLog(TOTPAuthenticator.class);


    /**
     * The name of the credential representing a user identifier.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String USERID_CREDENTIAL_NAME = "userid";

    /**
     * The name of the credential representing a username.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String USERNAME_CREDENTIAL_NAME = "username";

    /**
     * The name of the credential representing a username.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see Credential newCredential(String name, Object value)
     */
    public static final String SECRET_CREDENTIAL_NAME = "secret";



    private String id;

    private String crypto = "HmacSHA1";

    private int returnDigits = 6;

    private CredentialStore credentialStore;

    private String secretCredentialName = SECRET_CREDENTIAL_NAME;

    private TOTP totp;

    public void init() {
        totp =  new TOTP();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCrypto() {
        return crypto;
    }

    public void setCrypto(String crypto) {
        this.crypto = crypto;
    }

    public int getReturnDigits() {
        return returnDigits;
    }

    public void setReturnDigits(int returnDigits) {
        this.returnDigits = returnDigits;
    }

    public CredentialStore getCredentialStore() {
        return credentialStore;
    }

    public void setCredentialStore(CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
    }

    public String getSecretCredentialName() {
        return secretCredentialName;
    }

    public void setSecretCredentialName(String secretCredentialName) {
        this.secretCredentialName = secretCredentialName;
    }

    @Override
    public boolean canAuthenticate(Object requestToken) {
        if (requestToken instanceof UsernameTokenType){
            return ((UsernameTokenType)requestToken).getOtherAttributes().containsKey( new QName( PASSCODE_NS) );
        }
        return false;
    }

    @Override
    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {

        try {

            // Get username and token
            UsernameTokenType token = (UsernameTokenType) requestToken;
            String code = token.getOtherAttributes().get(new QName(PASSCODE_NS));

            if (code == null || "".equals(code))
                throw new SecurityTokenEmissionException("Code not received!");

            // Get current time
            long time = (System.currentTimeMillis() / 1000) / 30;
            String hexTime = Long.toHexString(time);

            if (logger.isDebugEnabled())
                logger.debug("Authenticating user " + token.getUsername().getValue() + ". Time: " + hexTime);

            if (credentialStore == null) {
                throw new SecurityTokenEmissionException("This authenticator requires a credential store!");
            }

            // Load credentials (username/secret)
            CredentialKey uk = new SimpleUserKey(token.getUsername().getValue());
            Credential[] creds = credentialStore.loadCredentials(uk, this);

            if (logger.isDebugEnabled())
                logger.debug("Loaded " + creds.length + " credentials");

            // Build a Subject:
            Subject subject = new Subject();

            Set principals = subject.getPrincipals();
            Set publicCredentials = subject.getPublicCredentials();
            Set privateCredentials = subject.getPrivateCredentials();

            Principal principal = null;

            boolean isValid = false;

            // Go through credentials, and validate the received code
            for (Credential cred : creds) {

                if (logger.isTraceEnabled())
                    logger.trace("Processing credential " + cred.getClass().getSimpleName());

                if (cred instanceof OTPSecret) {

                    // This is the OTP secret or key for the user (TODO : encryption ?!)
                    OTPSecret secret = (OTPSecret) cred;

                    String validCode = TOTP.generate(secret.getValue(), hexTime, returnDigits, crypto);

                    if (!code.equals(validCode)) {

                        if (logger.isTraceEnabled())
                            logger.trace("Invalid code " + code + " for " + token.getUsername().getValue());

                        throw new SecurityTokenAuthenticationFailure(getId(), "Invalid code!"); // TODO : Improve
                    }

                    isValid = true;
                    privateCredentials.add(secret);

                    if (logger.isTraceEnabled())
                        logger.trace("Valid code for " + token.getUsername().getValue());


                }

                if (cred instanceof UserNameCredential) {
                    publicCredentials.add(cred);
                    if (principal == null) {
                        principal = new SimplePrincipal((String) ((UserNameCredential) cred).getValue());
                    }
                }

                // Populate the Subject
                if (cred instanceof UserIdCredential) {
                    publicCredentials.add(cred);
                    principal = new SimplePrincipal((String) ((UserIdCredential) cred).getValue());
                }


            }

            if (!isValid) {

                if (logger.isTraceEnabled())
                    logger.trace("Unauthenticated code " + code + " for " + token.getUsername().getValue() + ". OTPSecret not available");

                throw new SecurityTokenAuthenticationFailure(getId(), "Code not received!");
            }

            if (principal == null) {
                logger.error("Unauthenticated code " + code + " for " + token.getUsername().getValue() + ", no credential for principal found!");
                throw new SecurityTokenAuthenticationFailure(getId(), "Principal not found for " + token.getUsername().getValue());
            }

            if (logger.isTraceEnabled())
                logger.trace("Authenticated code " + code + " for " + principal.getName());


            principals.add(principal);

            return subject;
        } catch (SSOIdentityException e) {
            logger.error(e.getMessage(), e);
            throw new SecurityTokenAuthenticationFailure(getId(), e);
        }


    }

    @Override
    public Credential newCredential(String name, Object value) {

        if (name.equalsIgnoreCase(USERID_CREDENTIAL_NAME)) {
            return new UserIdCredential(value);
        }

        if (name.equalsIgnoreCase(USERNAME_CREDENTIAL_NAME)) {
            return new UserNameCredential(value);
        }

        if (name.equalsIgnoreCase(getSecretCredentialName())) {
            return new OTPSecret((String) value);
        }

        // Don't know how to handle this name ...
        if (logger.isDebugEnabled())
            logger.debug("Unknown credential name : " + name);

        return null;
    }

    @Override
    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {

        List<Credential> creds = new ArrayList<Credential>();

        String value = UserUtil.getProperty(user, getSecretCredentialName());
        if (value != null)
            creds.add(newCredential(getSecretCredentialName(), value));

        creds.add(newCredential(USERNAME_CREDENTIAL_NAME, user.getUserName()));
        creds.add(newCredential(USERID_CREDENTIAL_NAME, user.getUserName()));

        return creds.toArray(new Credential[0]);
    }
}
