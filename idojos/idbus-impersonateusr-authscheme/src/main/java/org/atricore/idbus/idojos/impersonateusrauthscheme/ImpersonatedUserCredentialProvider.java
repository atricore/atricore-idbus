package org.atricore.idbus.idojos.impersonateusrauthscheme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ImpersonatedUserCredentialProvider implements CredentialProvider {

    /**
     * The name of the credential representing a password.
     * Used to get a new credential instance based on its name and value.
     * Value : password
     *
     * @see org.atricore.idbus.kernel.main.authn.Credential newCredential(String name, Object value)
     */
    public static final String CURRENT_USR_VALIDATION_CREDENTIAL_NAME = "currentUserValidation";


    /**
     * The name of the credential representing a username.
     * Used to get a new credential instance based on its name and value.
     * Value : username
     *
     * @see org.atricore.idbus.kernel.main.authn.Credential newCredential(String name, Object value)
     */
    public static final String IMPERSONATED_USERNAME_CREDENTIAL_NAME = "impersonatedUsername";

    private static final Log logger = LogFactory.getLog(ImpersonatedUserCredentialProvider.class);

    /**
     * Creates a new credential based on its name and value.
     *
     * @param name  the credential name
     * @param value the credential value
     * @return the Credential instance representing the supplied name-value pair.
     */
    public Credential newCredential(String name, Object value) {
        if (name.equalsIgnoreCase(IMPERSONATED_USERNAME_CREDENTIAL_NAME)) {
            return new ImpersonatedUsernameCredential(value);
        }

        if (name.equalsIgnoreCase(CURRENT_USR_VALIDATION_CREDENTIAL_NAME)) {
            return new CurrentUserValidationCredential(value);
        }

        // Don't know how to handle this name ...
        if (logger.isDebugEnabled())
            logger.debug("Unknown credential name : " + name);

        return null;

    }

    /**
     * Creates a new 'encoded credential'
     * @param name
     * @param value
     * @return
     */
    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {
        return new Credential[0];
    }
}
