package org.atricore.idbus.capabilities.spnego.authenticators;

import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Constants;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.namespace.QName;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class SpnegoSecurityTokenAuthenticator extends AbstractSecurityTokenAuthenticator {

    private static final String SCHEME_NAME = "spnego-authentication";

    public static final String SPNEGO_NS = "urn:org:atricore:idbus:kernel:main:authn:spnego";

    public static final String TICKET_NS = "urn:org:atricore:idbus:kernel:main:authn:ticket";

    public SpnegoSecurityTokenAuthenticator() {
        super();
        setScheme(SCHEME_NAME);

    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {

        BinarySecurityTokenType binaryToken = (BinarySecurityTokenType) requestToken;

        String spnegoSecurityToken = binaryToken.getOtherAttributes().get( new QName( SPNEGO_NS) );

        Credential spnegoCredential = getAuthenticator().newCredential(getScheme(), "spnegoSecurityToken", spnegoSecurityToken);
        return new Credential[] {spnegoCredential};
    }

    public boolean canAuthenticate(Object requestToken) {
        if (requestToken instanceof BinarySecurityTokenType ){
            return ((BinarySecurityTokenType)requestToken).getOtherAttributes().containsKey( new QName( SPNEGO_NS) );
        }
        return false;
    }
}
