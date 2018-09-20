package org.atricore.idbus.capabilities.csca;

import org.atricore.idbus.kernel.main.authn.BasePolicyEnforcementStatement;

import java.security.cert.X509Certificate;

public class X509CertificateNotValidAuthnStatement extends BasePolicyEnforcementStatement {

    public static final String NAMESPACE = "urn:org:atricore:idbus:authn:policy:invalid-cert";

    public static final String NAME = "invalidCert";

    private X509Certificate cert;

    public X509CertificateNotValidAuthnStatement(X509Certificate cert) {
        super(NAMESPACE, NAME);
        this.cert = cert;
    }

    public X509Certificate getCertificate() {
        return cert;
    }
}