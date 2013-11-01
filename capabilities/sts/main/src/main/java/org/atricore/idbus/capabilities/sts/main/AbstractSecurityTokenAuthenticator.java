package org.atricore.idbus.capabilities.sts.main;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.Authenticator;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.AuthenticationFailureException;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;

import javax.security.auth.Subject;

/**
 * This abstract class works as an adapter between IDBUS Authenticator (WS-Trust) and old plain JOSSO Authenticator components.
 *
 * Subclasses need to be able to transform a WS-Trust security token into a set of credentials.
 *
 *
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public abstract class AbstractSecurityTokenAuthenticator implements SecurityTokenAuthenticator, Constants {

    private static Log logger = LogFactory.getLog(AbstractSecurityTokenAuthenticator.class);

    private String id;

    private Authenticator auth;

    // This is actually the scheme type, like basic-authentication, etc.
    private String scheme;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public Authenticator getAuthenticator() {
        return auth;
    }

    public void setAuthenticator(Authenticator auth) {
        this.auth = auth;
    }


    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {
        Credential[] credentials = null;

        try {

            // Authenticate
            if (logger.isDebugEnabled())
                logger.debug("Authenticating " + requestToken.getClass().getSimpleName() + " using '" + getScheme() + "'");

            credentials = getCredentials(requestToken);
            if (logger.isTraceEnabled())
                logger.trace("Got " + (credentials != null ? credentials.length + "" : "<null>") + " credentials");

            // Adapt authentication, use existing components
            return getAuthenticator().check(credentials, getScheme());


        } catch (AuthenticationFailureException e) {


            throw new SecurityTokenAuthenticationFailure(getScheme(), e.getSSOPolicies(), e);

        } catch (SSOAuthenticationException e) {
            throw new SecurityTokenEmissionException(e);
        }

    }

    protected abstract Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException;

}
