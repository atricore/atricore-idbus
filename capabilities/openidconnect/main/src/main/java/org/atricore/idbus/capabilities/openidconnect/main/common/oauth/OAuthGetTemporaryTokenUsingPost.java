package org.atricore.idbus.capabilities.openidconnect.main.common.oauth;

import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;

public class OAuthGetTemporaryTokenUsingPost extends OAuthGetTemporaryToken {

    public OAuthGetTemporaryTokenUsingPost(String authorizationServerUrl) {
        super(authorizationServerUrl);
        this.usePost = true;
    }
}
