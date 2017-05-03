package org.atricore.idbus.capabilities.csca.authenticators;

import org.atricore.idbus.capabilities.sts.main.AbstractSecurityTokenAuthenticator;
import org.atricore.idbus.kernel.main.authn.Credential;
import org.atricore.idbus.kernel.main.authn.exceptions.SSOAuthenticationException;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.BinarySecurityTokenType;

import javax.xml.namespace.QName;

public class X509CertificateAuthenticator extends AbstractSecurityTokenAuthenticator {

    private static final String SCHEME_NAME = "clientcert-authentication";

    public static final String CSCA_NS = "urn:org:atricore:idbus:kernel:main:authn:csca";

    public X509CertificateAuthenticator() {
        super(SCHEME_NAME);
        setScheme(SCHEME_NAME);
    }

    @Override
    protected Credential[] getCredentials(Object requestToken) throws SSOAuthenticationException {

        BinarySecurityTokenType binaryToken = (BinarySecurityTokenType) requestToken;

        String cscaSecurityToken = binaryToken.getOtherAttributes().get( new QName( CSCA_NS) );

        Credential cscaCredential = getAuthenticator().newCredential(getScheme(), "cscaSecurityToken", cscaSecurityToken);
        return new Credential[] {cscaCredential};
    }

    public boolean canAuthenticate(Object requestToken) {
        if (requestToken instanceof BinarySecurityTokenType ){
            return ((BinarySecurityTokenType)requestToken).getOtherAttributes().containsKey( new QName( CSCA_NS) );
        }
        return false;
    }
}

