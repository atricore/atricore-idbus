package com.atricore.idbus.console.twofactor.wikid.authscheme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WiKIDCredentialProvider implements CredentialProvider {

    private static final Log logger = LogFactory.getLog(WiKIDCredentialProvider.class);

    public static final String PASSCODE_CREDENTIAL_NAME="passcode";

    public static final String USERNAME_CREDENTIAL_NAME="username";

    public Credential newCredential(String name, Object value) {
        if (name.equalsIgnoreCase(USERNAME_CREDENTIAL_NAME)) {
            return new WiKIDUsernameCredential((String) value);
        }

        if (name.equalsIgnoreCase(PASSCODE_CREDENTIAL_NAME)) {
            return new WiKIDPassCodeCredential((String) value);
        }

        // Don't know how to handle this name ...
        if (logger.isDebugEnabled())
            logger.debug("Unknown credential name : " + name);

        return null;
    }

    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }
}
