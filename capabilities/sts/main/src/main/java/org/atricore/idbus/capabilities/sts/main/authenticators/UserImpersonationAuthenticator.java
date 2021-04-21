package org.atricore.idbus.capabilities.sts.main.authenticators;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class UserImpersonationAuthenticator  extends AbstractSecurityTokenAuthenticator {

    public static final String SCHEME_NAME = "impersonate-authentication";

    public static final String USRVALIDATION_NS = "urn:org:atricore:idbus:kernel:main:authn:user-validation";

    private static Log logger = LogFactory.getLog(UserImpersonationAuthenticator.class);

    public UserImpersonationAuthenticator() {
        super(SCHEME_NAME);
        setScheme(SCHEME_NAME);
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

        String impersonatedUsername = usernameToken.getUsername().getValue();
        Object currentUserValidation = usernameToken.getOtherAttributes().get( new QName(USRVALIDATION_NS) );

        Credential impersonatedUsernameCredential = getAuthenticator().newCredential(getScheme(), "impersonatedUsername", impersonatedUsername);
        Credential currentUserValidationCredential = getAuthenticator().newCredential(getScheme(), "currentUserValidation", currentUserValidation);

        return new Credential[] {impersonatedUsernameCredential, currentUserValidationCredential};
    }

    public boolean canAuthenticate(Object requestToken) {
        if (requestToken instanceof UsernameTokenType) {
            UsernameTokenType usernameToken = (UsernameTokenType) requestToken;
            if (usernameToken.getOtherAttributes().get( new QName(USRVALIDATION_NS) ) != null)
                return true;
        }

        return false;
    }
}
