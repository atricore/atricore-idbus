package org.atricore.idbus.capabilities.sts.main.authenticators;

import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenAuthenticator;
import org.atricore.idbus.capabilities.sts.main.SecurityTokenEmissionException;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.UsernameTokenType;

import javax.security.auth.Subject;
import javax.xml.namespace.QName;

/**
 * Relies an inbound security token without incurring in authentication, emitting a corresponding subject.
 * Pass-through authentication is used for skipping the authentication step when it has been already carried out by
 * a third-party, such as a proxy.
 *
 * @author <a href=mailto:gbrigandi@atricore.org>Gianluca Brigandi</a>
 */
public class PassthroughSecurityTokenAuthenticator extends AbstractSecurityTokenAuthenticator {

    private QName proxyNs = new QName(Constants.PROXY_NS);

    private QName previousSessionNs = new QName(Constants.PREVIOUS_SESSION_NS);

    public PassthroughSecurityTokenAuthenticator() {
        super();
    }

    @Override
    public Subject authenticate(Object requestToken) throws SecurityTokenEmissionException {
        Subject outSubject = new Subject();
        UsernameTokenType usernameToken = (UsernameTokenType) requestToken;

        String username = usernameToken.getUsername().getValue();
        outSubject.getPrincipals().add(new SimplePrincipal(username));

        return outSubject;
    }


    public boolean canAuthenticate(Object requestToken) {

        if (requestToken instanceof UsernameTokenType) {

            UsernameTokenType usrToken = (UsernameTokenType) requestToken;

            // TODO : Make sure that this attributes cannot be set by anybody but a local IDP
            // Authenticate when PROXY attribute is set.
            if (usrToken.getOtherAttributes().get(proxyNs) != null) {
                return true;
            }

            // Authenticate when PROXY attribute is set.
            if (usrToken.getOtherAttributes().get(previousSessionNs) != null) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {
        throw new UnsupportedOperationException("Passthrough authentication does NOT provide credentials");
    }
}
