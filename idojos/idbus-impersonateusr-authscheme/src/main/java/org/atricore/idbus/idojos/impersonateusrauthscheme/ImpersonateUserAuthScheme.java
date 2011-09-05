package org.atricore.idbus.idojos.impersonateusrauthscheme;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.CredentialProvider;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.atricore.idbus.kernel.main.authn.scheme.AbstractAuthenticationScheme;

import java.security.Principal;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class ImpersonateUserAuthScheme  extends AbstractAuthenticationScheme {

    private static final Log logger = LogFactory.getLog(ImpersonateUserAuthScheme.class);

    private CurrentUserValidationPolicy policy;

    @Override
    protected CredentialProvider doMakeCredentialProvider() {
        return new ImpersonatedUserCredentialProvider();
    }

    public boolean authenticate() throws SSOAuthenticationException {
        setAuthenticated(false);

        if (policy == null) {
            if (logger.isTraceEnabled())
                logger.trace("No configured policy, cannot authenticate!");
            return false;
        }

        if (logger.isTraceEnabled())
            logger.trace("Using user verification policy : " + policy);

        String impersonatedUsername = getImpersonatedUsername(_inputCredentials);
        CurrentUserValidationCredential userValidation = getCurrentUserValidationCredential(_inputCredentials);

        if (logger.isTraceEnabled())
            logger.trace("Impersonated username " + impersonatedUsername + " for " + userValidation);



        boolean authenticated = policy.canImpersonate(impersonatedUsername, userValidation);

        if (logger.isDebugEnabled())
            logger.debug("User impersonation was "+(authenticate() ? "" : "NOT") + " allowed for ["+userValidation.toString()+"] as ["+impersonatedUsername+"]");

        setAuthenticated(authenticated);

        return authenticated;
    }

    public Principal getPrincipal() {
        return new SimplePrincipal(getImpersonatedUsername(_inputCredentials));
    }

    public Principal getPrincipal(Credential[] credentials) {
        return new SimplePrincipal(getImpersonatedUsername(credentials));
    }

    public Credential[] getPrivateCredentials() {
        Credential c = getCurrentUserValidationCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;
    }

    public Credential[] getPublicCredentials() {
        Credential c = getImpersonatedUsernameCredential(_inputCredentials);
        if (c == null)
            return new Credential[0];

        Credential[] r = {c};
        return r;
    }

    protected String getImpersonatedUsername(Credential[] credentials) {
        ImpersonatedUsernameCredential c = getImpersonatedUsernameCredential(credentials);
        if (c == null)
            return null;

        return (String) c.getValue();
    }

    protected ImpersonatedUsernameCredential getImpersonatedUsernameCredential(Credential[] credentials) {

        for (Credential credential : credentials) {
            if (credential instanceof ImpersonatedUsernameCredential) {
                return (ImpersonatedUsernameCredential) credential;
            }
        }
        return null;
    }

    protected CurrentUserValidationCredential getCurrentUserValidationCredential(Credential[] credentials) {
        for (int i = 0; i < credentials.length; i++) {
            if (credentials[i] instanceof CurrentUserValidationCredential) {
                return (CurrentUserValidationCredential) credentials[i];
            }
        }
        return null;
    }



}
