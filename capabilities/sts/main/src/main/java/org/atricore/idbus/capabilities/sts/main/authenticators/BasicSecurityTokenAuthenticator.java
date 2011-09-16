package org.atricore.idbus.capabilities.sts.main.authenticators;

import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;

/**
 * Default basic authenticator adapter.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BasicSecurityTokenAuthenticator extends AbstractSecurityTokenAuthenticator {

    public static final String SCHEME_NAME = "basic-authentication";

    public BasicSecurityTokenAuthenticator() {
        super();
        setScheme(SCHEME_NAME);
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        setScheme(SCHEME_NAME);

        UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

        String username = usernameToken.getUsername().getValue();
        String password = usernameToken.getOtherAttributes().get( new QName( PASSWORD_NS) );

        Credential usernameCredential = getAuthenticator().newCredential(getScheme(), "username", username);
        Credential passwordCredential = getAuthenticator().newCredential(getScheme(), "password", password);

        return new Credential[] {usernameCredential, passwordCredential};
    }

    public boolean canAuthenticate(Object requestToken) {

        if (requestToken instanceof UsernameTokenType) {
            UsernameTokenType usernameToken = (UsernameTokenType) requestToken;
            if (usernameToken.getOtherAttributes().get( new QName( PASSWORD_NS) ) != null)
                return true;
        }

        return false;
    }
}
