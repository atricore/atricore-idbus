package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import org.atricore.idbus.kernel.main.authn.BaseCredential;

/**
 * Created by sgonzalez.
 */
public class AuthorizationCodeGrantCredential extends BaseCredential {

    public AuthorizationCodeGrantCredential(Object value) {
        super(value);
    }

    public AuthorizationCodeGrant getAuthzCodeGrant() {
        return (AuthorizationCodeGrant) getValue();
    }


}
