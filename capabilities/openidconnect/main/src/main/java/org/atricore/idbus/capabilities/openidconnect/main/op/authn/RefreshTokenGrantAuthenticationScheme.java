package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.openidconnect.main.op.AuthorizationGrant;
import org.atricore.idbus.capabilities.sts.main.TokenStore;
import org.atricore.idbus.kernel.main.authn.*;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AbstractAuthenticationScheme;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Set;

public class RefreshTokenGrantAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Log logger = LogFactory.getLog(RefreshTokenGrantAuthenticationScheme.class);

    private TokenStore tokenStore;

    // Authorization Code emitted previously
    private SecurityToken securityToken;

    public RefreshTokenGrantAuthenticationScheme() {
        this.setName(RefreshTokenGrantAuthenticator.SCHEME_NAME);
    }

    /**
     * Use an authz grant credential provider.  Refresh tokens are also authz grants for this purpose.
     * @return
     */
    @Override
    protected CredentialProvider doMakeCredentialProvider() {
        return new AuthorizationGrantCredentialProvider();
    }

    @Override
    public boolean authenticate() throws SSOAuthenticationException {
        Credential[] knownCredentials = getKnownCredentials();

        if (knownCredentials == null || knownCredentials.length < 1) {
            logger.trace("No known credentials found");
            return false;
        }

        RefreshTokenGrantCredential expectedCredential = (RefreshTokenGrantCredential) knownCredentials[0];
        RefreshToken expectedToken = expectedCredential.getRefreshToken().getRefreshToken();

        RefreshTokenGrantCredential receivedCredential = (RefreshTokenGrantCredential) _inputCredentials[0];
        RefreshToken receivedToken = (RefreshToken) receivedCredential.getRefreshToken().getRefreshToken();

        if (!expectedToken.getValue().equals(receivedToken.getValue())) {
            logger.debug("Invalid authorization code rcv: " + receivedToken.getValue());
            logger.trace("Invalid authorization code rcv/exp: " + receivedToken.getValue() + "/" + expectedToken.getValue());
            return false;
        }

        if (securityToken.getExpiresOn() < System.currentTimeMillis()) {
            logger.trace ("Expired refresh token : " + receivedToken.getValue());
            return false;
        }

        setAuthenticated(true);

        return true;
    }

    @Override
    public Principal getInputPrincipal() {
        return getPrincipal();
    }

    @Override
    public Principal getPrincipal() {
        return new SimplePrincipal(securityToken.getNameIdentifier());
    }

    @Override
    public Principal getPrincipal(Credential[] credentials) {

        if (credentials.length == 1) {

            Subject s = null;
            if (credentials[0] instanceof RefreshTokenGrantCredential) {
                if (logger.isDebugEnabled())
                    logger.debug("Using subject from AuthorizationGrantCredential");

                RefreshTokenGrantCredential c = (RefreshTokenGrantCredential) credentials[0];
                SecurityToken st = tokenStore.retrieve(c.getRefreshToken().getRefreshToken().getValue());

                SimplePrincipal p = new SimplePrincipal(st.getNameIdentifier());

                return p;
            }

        }

        return null;
    }

    @Override
    public Credential[] getPrivateCredentials() {
        return new Credential[0];
    }

    @Override
    public Credential[] getPublicCredentials() {
        return _inputCredentials;
    }

    @Override
    protected Credential[] getKnownCredentials() throws SSOAuthenticationException {
        RefreshTokenGrantCredential receviedCredential = (RefreshTokenGrantCredential) this._inputCredentials[0];
        com.nimbusds.oauth2.sdk.RefreshTokenGrant receivedAuthzGrant = (com.nimbusds.oauth2.sdk.RefreshTokenGrant) receviedCredential.getValue();
        securityToken = tokenStore.retrieve(receivedAuthzGrant.getRefreshToken().getValue());

        if (securityToken == null) {
            logger.debug("Unknown authorization grant " + receivedAuthzGrant.toString());
            return null;
        }

        RefreshToken expectedToken  = (RefreshToken) securityToken.getContent();
        RefreshTokenGrant expectedGrant = new RefreshTokenGrant(expectedToken);
        Credential expectedCredential = new RefreshTokenGrantCredential(expectedGrant);

        return new Credential[] { expectedCredential };
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}
