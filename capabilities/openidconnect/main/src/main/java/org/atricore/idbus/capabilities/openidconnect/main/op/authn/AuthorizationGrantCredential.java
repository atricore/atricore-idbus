package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import org.atricore.idbus.capabilities.openidconnect.main.op.AuthorizationGrant;
import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * Created by sgonzalez.
 */
public class AuthorizationGrantCredential extends BaseCredential {

    public AuthorizationGrantCredential(Object credential) {
        super(credential);
    }

    public AuthorizationGrant getAuthzGrant() {
        return (AuthorizationGrant) getValue();
    }

}
