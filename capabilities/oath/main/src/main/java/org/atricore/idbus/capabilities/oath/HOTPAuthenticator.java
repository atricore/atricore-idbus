package org.atricore.idbus.capabilities.oath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

import javax.security.auth.Subject;

/**
 * TODO : rfu
 *
 * This is couter based, we need to keep track of the number of emitted tokens for each user, and
 * use a way of syncing counters.
 */
public class HOTPAuthenticator implements SecurityTokenAuthenticator, CredentialProvider {


    private static final Log logger = LogFactory.getLog(HOTPAuthenticator.class);

    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean canAuthenticate(Object requestToken) {
        return false;
    }

    @Override
    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {
        return null;
    }

    @Override
    public Credential newCredential(String name, Object value) {
        return null;
    }

    @Override
    public Credential newEncodedCredential(String name, Object value) {
        return null;
    }

    @Override
    public Credential[] newCredentials(User user) {
        return new Credential[0];
    }
}
