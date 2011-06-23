package org.atricore.idbus.capabilities.sts.main.authenticators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;


/**
 * Default 2F Authenticator adapter.
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class TwoFactorSecurityTokenAuthenticator extends AbstractSecurityTokenAuthenticator {

    public static final String SCHEME_NAME = "2factor-authentication";

    public static final String PASSCODE_NS = "urn:org:atricore:idbus:kernel:main:authn:passcode";

    private static Log logger = LogFactory.getLog(TwoFactorSecurityTokenAuthenticator.class);

    public TwoFactorSecurityTokenAuthenticator() {
        super();
        setScheme(SCHEME_NAME);
    }

    public boolean canAuthenticate(Object requestToken) {
        if (requestToken instanceof UsernameTokenType ){
            return ((UsernameTokenType)requestToken).getOtherAttributes().containsKey( new QName( PASSCODE_NS) );
        }
        return false;
    }

    /**
     * Retreive credentials from Request Token.  UsernameTokenType is expected,
     * where username maps to username and password to 2fa passcode
     *
     * @param requestToken
     * @return
     * @throws org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException
     */
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

        String username = usernameToken.getUsername().getValue();
        String passcode = usernameToken.getOtherAttributes().get( new QName( PASSCODE_NS) );

        Credential usernameCredential = getAuthenticator().newCredential(getScheme(), "username", username);
        Credential passcodeCredential = getAuthenticator().newCredential(getScheme(), "passcode", passcode);

        return new Credential[] {usernameCredential, passcodeCredential};

    }
}
