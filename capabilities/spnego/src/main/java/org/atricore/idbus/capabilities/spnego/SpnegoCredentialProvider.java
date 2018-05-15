package org.atricore.idbus.capabilities.spnego;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class SpnegoCredentialProvider implements CredentialProvider {

    private static final Log logger = LogFactory.getLog(SpnegoCredentialProvider.class);

    public static final String SPNEGO_CREDENTIAL_NAME="spnegoSecurityToken";

    public Credential newCredential(String name, Object value) {
        if (name.equalsIgnoreCase(SPNEGO_CREDENTIAL_NAME)) {
            return new SpnegoTokenCredential((String) value);
        }

        // Don't know how to handle this name ...
        if (logger.isDebugEnabled())
            logger.debug("Unknown credential name : " + name);

        return null;
    }

    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {
        return new Credential[0];
    }
}
