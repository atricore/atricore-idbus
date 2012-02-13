package org.atricore.idbus.capabilities.atricoreid.connector.java;

import org.atricore.idbus.capabilities.atricoreid.common.*;
import org.atricore.idbus.capabilities.atricoreid.common.util.JasonUtils;

import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SecureAccessTokenResolverImpl implements AccessTokenResolver {

    private TokenSigner tokenSigner;

    private TokenEncrypter tokenEncrypter;

    private long tkValidityInterval;

    public AtricoreIDAccessToken resolve(String tokenString) throws AtricoreIDRServerException {
        try {

            AtricoreIDAccessTokenEnvelope envelope = JasonUtils.unmarshalAccessTokenEnvelope(tokenString);

            String accessToken = envelope.getToken();
            // Verify signature, if any
            if (!tokenSigner.isValid(envelope.getToken(), envelope.getSignatureValue())) {
                throw new AtricoreIDRServerException("Invalid Token Signature ["+envelope.getSignatureValue()+
                        "] for ["+envelope.getToken()+"]");
            }

            accessToken = tokenEncrypter.decrypt(envelope.getToken());
            if (envelope.isDeflated())
                accessToken = JasonUtils.inflate(accessToken, true);

            AtricoreIDAccessToken at = JasonUtils.unmarshalAccessToken(accessToken);
            if (getTokenValidityInterval() > 0) {
                long now = System.currentTimeMillis();
                if (now - at.getTimeStamp() > getTokenValidityInterval() * 1000L) {
                    throw new AtricoreIDTokenExpiredException("Token is over " + getTokenValidityInterval() + " seconds old ["+now+"/"+at.getTimeStamp()+"]");
                }
            }

            return at;

        } catch (IOException e) {
            throw new AtricoreIDRServerException(e);
        } catch (AtricoreIDSignatureException e) {
            throw new AtricoreIDRServerException(e);
        } catch (AtricoreIDEncryptionException e) {
            throw new AtricoreIDRServerException(e);
        }
    }

    public TokenSigner getTokenSigner() {
        return tokenSigner;
    }

    public void setTokenSigner(TokenSigner tokenSigner) {
        this.tokenSigner = tokenSigner;
    }

    public TokenEncrypter getTokenEncrypter() {
        return tokenEncrypter;
    }

    public void setTokenEncrypter(TokenEncrypter tokenEncrypter) {
        this.tokenEncrypter = tokenEncrypter;
    }

    /**
     * In seconds
     */
    public long getTokenValidityInterval() {
        return tkValidityInterval;
    }

    public void setTokenValidityInterval(long tkValidityInterval) {
        this.tkValidityInterval = tkValidityInterval;
    }
}
