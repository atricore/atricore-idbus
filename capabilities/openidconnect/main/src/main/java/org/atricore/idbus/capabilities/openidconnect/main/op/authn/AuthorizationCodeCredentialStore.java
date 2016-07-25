package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import org.atricore.idbus.capabilities.sts.main.TokenStore;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialKey;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.store.exceptions.SSOIdentityException;
import org.atricore.idbus.kernel.main.store.identity.CredentialStore;

/**
 * Created by sgonzalez.
 */
public class AuthorizationCodeCredentialStore implements CredentialStore {

    private TokenStore tokenStore;

    @Override
    public Credential[] loadCredentials(CredentialKey key, CredentialProvider cp) throws SSOIdentityException {
        return new Credential[0];
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}
