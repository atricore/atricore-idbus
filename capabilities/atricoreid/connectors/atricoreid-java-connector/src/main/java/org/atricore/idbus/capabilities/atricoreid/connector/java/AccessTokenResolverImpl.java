package org.atricore.idbus.capabilities.atricoreid.connector.java;

import org.atricore.idbus.capabilities.atricoreid.common.AtricoreIDAccessToken;
import org.atricore.idbus.capabilities.atricoreid.common.AtricoreIDAccessTokenEnvelope;
import org.atricore.idbus.capabilities.atricoreid.common.util.JasonUtils;

import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AccessTokenResolverImpl implements AccessTokenResolver {


    public AtricoreIDAccessToken resolve(String tokenString) throws AtricoreIDRServerException {
        try {
            AtricoreIDAccessTokenEnvelope envelope = JasonUtils.unmarshalAccessTokenEnvelope(tokenString);

            String accessToken = envelope.getToken();
            if (envelope.isDeflated())
                accessToken = JasonUtils.inflate(accessToken, true);

            return JasonUtils.unmarshalAccessToken(accessToken);

        } catch (IOException e) {
            throw new AtricoreIDRServerException(e);
        }
    }
}
