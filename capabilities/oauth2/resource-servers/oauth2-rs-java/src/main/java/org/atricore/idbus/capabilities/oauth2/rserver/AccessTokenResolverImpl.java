package org.atricore.idbus.capabilities.oauth2.rserver;

import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessTokenEnvelope;
import org.atricore.idbus.capabilities.oauth2.common.util.JasonUtils;

import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccessTokenResolverImpl implements AccessTokenResolver {


    public OAuth2AccessToken resolve(String tokenString) throws OAuth2RServerException {
        try {
            OAuth2AccessTokenEnvelope envelope = JasonUtils.unmarshalAccessTokenEnvelope(tokenString);

            String accessToken = envelope.getToken();
            if (envelope.isDeflated())
                accessToken = JasonUtils.inflate(accessToken, true);

            return JasonUtils.unmarshalAccessToken(accessToken);

        } catch (IOException e) {
            throw new OAuth2RServerException(e);
        }
    }
}
