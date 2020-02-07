package org.atricore.idbus.capabilities.sts.main.authenticators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.UsernamePasswordCredentialProvider;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;
import java.util.Map;

/**
 * Default basic authenticator adapter.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class BasicSecurityTokenAuthenticator extends AbstractSecurityTokenAuthenticator {

    private static final Log logger = LogFactory.getLog(BasicSecurityTokenAuthenticator.class);

    public static final String SCHEME_NAME = "basic-authentication";

    public BasicSecurityTokenAuthenticator() {
        super(SCHEME_NAME);
        setScheme(SCHEME_NAME);
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        setScheme(SCHEME_NAME);

        UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

        String userid = usernameToken.getUsername().getValue();
        String password = usernameToken.getOtherAttributes().get( new QName( PASSWORD_NS) );

        Credential userIdCredential = getAuthenticator().newCredential(getScheme(), UsernamePasswordCredentialProvider.USERID_CREDENTIAL_NAME, userid);
        Credential passwordCredential = getAuthenticator().newCredential(getScheme(), UsernamePasswordCredentialProvider.PASSWORD_CREDENTIAL_NAME, password);

        return new Credential[] {userIdCredential, passwordCredential};
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
