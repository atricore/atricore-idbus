package org.atricore.idbus.capabilities.oauth2.main.sso;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2AccessToken;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2Claim;
import org.atricore.idbus.capabilities.oauth2.common.OAuth2ClaimType;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolver;
import org.atricore.idbus.capabilities.oauth2.rserver.AccessTokenResolverFactory;
import org.atricore.idbus.capabilities.oauth2.rserver.OAuth2RServerException;
import org.atricore.idbus.capabilities.oauth2.rserver.SecureAccessTokenResolverFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AbstractAuthenticationScheme;

import java.security.Principal;
import java.util.Properties;

/**
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class OAuth2AuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Log logger = LogFactory.getLog(OAuth2AuthenticationScheme.class);

    private OAuth2AccessToken oauth2AccessToken;

    private String sharedSecret;
    private String signKey;
    private String encryptKey;
    private String accessTokenValidityInterval;

    public OAuth2AuthenticationScheme() {
        this.setName("oauth2-authentication");
    }

    @Override
    protected CredentialProvider doMakeCredentialProvider() {
        return new OAuth2CredentialProvider();
    }

    public boolean authenticate() throws SSOAuthenticationException {

        try {

            String oauth2AccessToken = getOAuth2AccessToken(_inputCredentials);

            AccessTokenResolverFactory atrf = new SecureAccessTokenResolverFactory();

            Properties config = new Properties();
            config.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_ENC_PROPERTY, sharedSecret);
            config.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_SIGN_PROPERTY, signKey);
            config.setProperty(SecureAccessTokenResolverFactory.SHARED_SECRECT_ENC_PROPERTY, encryptKey);
            config.setProperty(SecureAccessTokenResolverFactory.TOKEN_VALIDITY_INTERVAL_PROPERTY,
                    accessTokenValidityInterval);
            
            atrf.setConfig(config);
            AccessTokenResolver ats = atrf.newResolver();
            this.oauth2AccessToken = ats.resolve(oauth2AccessToken);
            
            setAuthenticated(true);

            return isAuthenticated();

        } catch ( OAuth2RServerException e) {
            if (logger.isDebugEnabled())
                logger.debug("OAuth2 Authentication Failure : " + e.getMessage(), e);

            throw new AuthenticationFailureException("Authentication failed : " + e.getMessage());

        } catch (Exception e) {
            throw new SSOAuthenticationException(e);
        }
    }

    public Principal getPrincipal() {

        String nameId = null;
        for (OAuth2Claim claim : oauth2AccessToken.getClaims()) {
            if (claim.getType().equals(OAuth2ClaimType.USERID.name()))
                nameId = claim.getValue();
        }

        if (nameId == null)
            return null;

        return new SimplePrincipal(nameId);
    }

    public Principal getPrincipal(Credential[] credentials) {
        return new SimplePrincipal(getOAuth2AccessToken(credentials));
    }

    /**
     * Only one password credential supported.
     */
    public Credential[] getPrivateCredentials() {
        Credential c = getOAuth2AccessTokenCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;
    }

    public Credential[] getPublicCredentials() {
        return new Credential[0];
    }

    protected String getOAuth2AccessToken(Credential[] credentials) {
        OAuth2AccessTokenCredential c = getOAuth2AccessTokenCredential(credentials);
        if (c == null)
            return null;

        return (String) c.getValue();
    }

    /**
     * Gets the credential that represents a Username.
     */
    protected OAuth2AccessTokenCredential getOAuth2AccessTokenCredential(Credential[] credentials) {

        for (Credential credential : credentials) {
            if (credential instanceof OAuth2AccessTokenCredential) {
                return (OAuth2AccessTokenCredential) credential;
            }
        }
        return null;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getSignKey() {
        return signKey;
    }

    public void setSignKey(String signKey) {
        this.signKey = signKey;
    }

    public String getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(String encryptKey) {
        this.encryptKey = encryptKey;
    }

    public String getAccessTokenValidityInterval() {
        return accessTokenValidityInterval;
    }

    public void setAccessTokenValidityInterval(String accessTokenValidityInterval) {
        this.accessTokenValidityInterval = accessTokenValidityInterval;
    }



}
