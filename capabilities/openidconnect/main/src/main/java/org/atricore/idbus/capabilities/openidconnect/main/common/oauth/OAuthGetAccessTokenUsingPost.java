package org.atricore.idbus.capabilities.openidconnect.main.common.oauth;

import com.google.api.client.auth.oauth.OAuthGetAccessToken;

public class OAuthGetAccessTokenUsingPost extends OAuthGetAccessToken {

    public OAuthGetAccessTokenUsingPost(String authorizationServerUrl) {
        super(authorizationServerUrl);
        this.usePost = true;
    }
}
