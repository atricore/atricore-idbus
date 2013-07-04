package org.atricore.idbus.capabilities.oauth2.rserver;

import org.atricore.idbus.capabilities.oauth2.common.*;
import org.atricore.idbus.capabilities.oauth2.common.util.JasonUtils;

import java.io.IOException;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SecureAccessTokenResolverImpl implements AccessTokenResolver {

    private TokenSigner tokenSigner;

    private TokenEncrypter tokenEncrypter;

    private long tkValidityInterval;

    public OAuth2AccessToken resolve(String tokenString) throws OAuth2RServerException {
        try {

            OAuth2AccessTokenEnvelope envelope = JasonUtils.unmarshalAccessTokenEnvelope(tokenString);

            String accessToken = envelope.getToken();
            // Verify signature, if any
            if (!tokenSigner.isValid(envelope.getToken(), envelope.getSignatureValue())) {
                throw new OAuth2RServerException("Invalid Token Signature ["+envelope.getSignatureValue()+
                        "] for ["+envelope.getToken()+"]");
            }

            accessToken = tokenEncrypter.decrypt(envelope.getToken());
            if (envelope.isDeflated())
                accessToken = JasonUtils.inflate(accessToken, true);

            OAuth2AccessToken at = JasonUtils.unmarshalAccessToken(accessToken);
            if (getTokenValidityInterval() > 0) {
                long now = System.currentTimeMillis();
                if (now - at.getTimeStamp() > getTokenValidityInterval() * 1000L) {
                    throw new OAuth2TokenExpiredException("Token is over " + getTokenValidityInterval() + " seconds old ["+now+"/"+at.getTimeStamp()+"]");
                }
            }

            return at;

        } catch (IOException e) {
            throw new OAuth2RServerException(e);
        } catch (OAuth2SignatureException e) {
            throw new OAuth2RServerException(e);
        } catch (OAuth2EncryptionException e) {
            throw new OAuth2RServerException(e);
        } catch (Exception e) {
            throw new OAuth2RServerException(e);
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
