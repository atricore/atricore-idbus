package org.atricore.idbus.capabilities.sts.main;

import javax.security.auth.Subject;

/**
 * Useful to add priority to custom authenticators.
 *
 */
public class SecurityTokenAuthnWrapper implements SecurityTokenAuthenticator {

    private SecurityTokenAuthenticator authn;

    private int priority;

    public SecurityTokenAuthnWrapper(SecurityTokenAuthenticator authn) {
        this.authn = authn;
    }

    @Override
    public String getId() {
        return authn.getId();
    }

    @Override
    public boolean canAuthenticate(Object requestToken) {
        return authn.canAuthenticate(requestToken);
    }

    @Override
    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {
        return authn.authenticate(requestToken);
    }

    @Override
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
