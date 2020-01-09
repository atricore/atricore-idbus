package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import org.atricore.idbus.kernel.main.authn.BaseCredential;

public class RefreshTokenGrantCredential extends BaseCredential {

    public RefreshTokenGrantCredential(Object value) {
        super(value);
    }

    public RefreshTokenGrant getRefreshToken() {
        return (RefreshTokenGrant) getValue();
    }


}
