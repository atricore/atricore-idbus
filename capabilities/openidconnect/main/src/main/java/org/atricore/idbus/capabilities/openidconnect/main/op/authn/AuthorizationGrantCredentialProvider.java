package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import org.atricore.idbus.capabilities.openidconnect.main.op.AuthorizationGrant;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.provisioning.domain.User;

/**
 * Created by sgonzalez.
 */
public class AuthorizationGrantCredentialProvider implements CredentialProvider {

    @Override
    public Credential newCredential(String name, Object value) {
        if (name != null) {
            AuthorizationGrant authzGrant = (AuthorizationGrant) value;
            return new AuthorizationCodeGrantCredential(authzGrant);
        }


        return null;
    }

    @Override
    public Credential newEncodedCredential(String name, Object value) {
        return newCredential(name, value);
    }

    @Override
    public Credential[] newCredentials(User user) {
        return new Credential[0];
    }
}