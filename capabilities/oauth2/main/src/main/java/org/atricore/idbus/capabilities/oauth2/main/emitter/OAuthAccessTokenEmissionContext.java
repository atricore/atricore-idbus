package org.atricore.idbus.capabilities.oauth2.main.emitter;

import org.atricore.idbus.common.oauth._2_0.protocol.OAuthAccessTokenType;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuthAccessTokenEmissionContext {


    private OAuthAccessTokenType accessToken;

    public void setOAuthAccessToken(OAuthAccessTokenType accessToken) {
        this.accessToken = accessToken;
    }
}
