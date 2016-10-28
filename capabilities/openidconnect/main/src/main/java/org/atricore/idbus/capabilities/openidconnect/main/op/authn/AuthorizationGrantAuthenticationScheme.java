package org.atricore.idbus.capabilities.openidconnect.main.op.authn;

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

/**
 * TODO: This must be able to authenticate a token request by processing an authorization grant.
 */
public class AuthorizationGrantAuthenticationScheme extends AbstractAuthenticationScheme {

    private static final Log logger = LogFactory.getLog(AuthorizationGrantAuthenticationScheme.class);

    private TokenStore tokenStore;

    // Authorization Code emitted previously
    private SecurityToken securityToken;

    public AuthorizationGrantAuthenticationScheme() {
        this.setName(AuthorizationGrantAuthenticator.SCHEME_NAME);
    }

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

        AuthorizationGrantCredential expectedAuthzGrant = (AuthorizationGrantCredential) knownCredentials[0];

        AuthorizationCodeGrantCredential receivedAuthzCodeGrant = (AuthorizationCodeGrantCredential) _inputCredentials[0];
        com.nimbusds.oauth2.sdk.AuthorizationCodeGrant rc = (com.nimbusds.oauth2.sdk.AuthorizationCodeGrant) receivedAuthzCodeGrant.getValue();

        if (!expectedAuthzGrant.getAuthzGrant().getId().equals(rc.getAuthorizationCode().getValue())) {
            logger.debug("Invalid authorization code rcv: " + rc.getAuthorizationCode().getValue());
            logger.trace("Invalid authorization code rcv/exp: " + rc.getAuthorizationCode().getValue() + "/" + expectedAuthzGrant.getAuthzGrant().getId());
            return false;
        }

        if (expectedAuthzGrant.getAuthzGrant().getExpiresOn() < System.currentTimeMillis()) {
            logger.debug("Expired token : " + rc.getAuthorizationCode().getValue());
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
        AuthorizationGrant authzGrant = (AuthorizationGrant) securityToken.getContent();

        // We should have an SSOUser here:
        Set<SSOUser> ssoUsers = authzGrant.getSubject().getPrincipals(SSOUser.class);
        if (ssoUsers.size() == 1) {
            SSOUser ssoUser = ssoUsers.iterator().next();
            return new SimplePrincipal(ssoUser.getName());
        }

        logger.error("No SSOUser principal found in retrieved security token " + securityToken.getId());

        return null;
    }

    @Override
    public Principal getPrincipal(Credential[] credentials) {

        if (credentials.length == 1) {

            Subject s = null;
            if (credentials[0] instanceof AuthorizationGrantCredential) {
                if (logger.isDebugEnabled())
                    logger.debug("Using subject from AuthorizationGrantCredential");
                s = ((AuthorizationGrantCredential) credentials[0]).getAuthzGrant().getSubject();

            } else if (credentials[0] instanceof AuthorizationCodeGrantCredential) {
                if (logger.isDebugEnabled())
                    logger.debug("Using subject from AuthorizationCodeGrantCredential");

                AuthorizationCodeGrantCredential c = (AuthorizationCodeGrantCredential) credentials[0];
                SecurityToken st = tokenStore.retrieve(c.getAuthzCodeGrant().getAuthorizationCode().getValue());

                AuthorizationGrant a = (AuthorizationGrant) st.getContent();
                s = a.getSubject();
            }

            if (s != null) {
                Set<SSOUser> ssoUsers = s.getPrincipals(SSOUser.class);

                if (ssoUsers.size() == 1) {
                    SSOUser ssoUser = ssoUsers.iterator().next();
                    return new SimplePrincipal(ssoUser.getName());
                } else {
                    logger.debug("No SSOUsers in subject for credential "  + credentials[0]);
                }
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
        AuthorizationCodeGrantCredential receviedCredential = (AuthorizationCodeGrantCredential) this._inputCredentials[0];
        com.nimbusds.oauth2.sdk.AuthorizationCodeGrant receivedAuthzGrant = (com.nimbusds.oauth2.sdk.AuthorizationCodeGrant) receviedCredential.getValue();
        securityToken = tokenStore.retrieve(receivedAuthzGrant .getAuthorizationCode().getValue());

        if (securityToken == null) {
            logger.debug("Unknown authorization grant " + receivedAuthzGrant.toString());
            return null;
        }

        AuthorizationGrant expectedAuthzGrant  = (AuthorizationGrant) securityToken.getContent();
        Credential expectedCredential = new AuthorizationGrantCredential(expectedAuthzGrant);

        return new Credential[] { expectedCredential };
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public void setTokenStore(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }
}
