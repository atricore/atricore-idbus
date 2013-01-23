package org.atricore.idbus.capabilities.clientcertauthn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.atricore.idbus.kernel.main.authn.SimplePrincipal;

import java.security.cert.X509Certificate;

/**
 * Created with IntelliJ IDEA.
 * User: sgonzalez
 * Date: 10/24/12
 * Time: 9:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class CertificatePrincipal extends SimplePrincipal {

    private static final Log logger = LogFactory.getLog(CertificatePrincipal.class);
    private X509Certificate _certificate;

    public CertificatePrincipal() {
    }

    public CertificatePrincipal(String username) {
        super(username);
    }

    public CertificatePrincipal(String username, X509Certificate certificate) {
        super(username);
        _certificate = certificate;
    }

    public X509Certificate getCertificate() {
        return _certificate;
    }

    /**
     * Compare this CertificatePrincipal against another CertificatePrincipal.
     *
     * @return true if name and certificate equals another.getName() and another.getCertificate()
     */
    public boolean equals(Object another) {
        if (!(another instanceof CertificatePrincipal)) {
            return false;
        }

        X509Certificate anotherCertificate = ((CertificatePrincipal) another).getCertificate();

        boolean equals = super.equals(another);
        if (!equals) {
            return false;
        }

        if (getCertificate() == null) {
            equals = anotherCertificate == null;
        } else {
            equals = getCertificate().equals(anotherCertificate);
        }

        return equals;
    }

    /**
     * Returns the hashcode of the certificate
     */
    public int hashCode() {
        return (getCertificate() == null ? 0 : getCertificate().hashCode());
    }

    public String toString() {
        if (getCertificate() != null) {
            return getCertificate().getSubjectX500Principal().getName() + " / " +
                    getCertificate().getIssuerX500Principal().getName();
        } else {
            return getName();
        }
    }

}
