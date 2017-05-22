package org.atricore.idbus.capabilities.csca.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.security.cert.*;
import java.util.Collections;
import java.util.Set;

/**
 * CRL X509 Certificate validator.
 *
 * @org.apache.xbean.XBean element="crl-validator"
 */
public class CRLX509CertificateValidator extends AbstractX509CertificateValidator {

    private static final Log log = LogFactory
            .getLog(CRLX509CertificateValidator.class);

    public void validate(X509Certificate certificate)
            throws X509CertificateValidationException {

        try {
            URL crlUrl = null;
            if (_url != null) {
                crlUrl = new URL(_url);
                log.debug("Using the CRL server at: " + _url);
            } else {
                log.debug("Using the CRL server specified in the certificate.");
                System.setProperty("com.sun.security.enableCRLDP", "true");
            }


            // TODO STRONG-AUTH, Make this a system configuration
            // configure the proxy
            if (_httpProxyHost != null && _httpProxyPort != null) {
                System.setProperty("http.proxyHost", _httpProxyHost);
                System.setProperty("http.proxyPort", _httpProxyPort);
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }

            // get certificate path
            CertPath cp = generateCertificatePath(certificate);

            // get trust anchors
            Set<TrustAnchor> trustedCertsSet = generateTrustAnchors();

            // init PKIX parameters
            PKIXParameters params = new PKIXParameters(trustedCertsSet);

            // activate certificate revocation checking
            params.setRevocationEnabled(true);

            // disable OCSP
            Security.setProperty("ocsp.enable", "false");

            // get a certificate revocation list
            if (crlUrl != null) {
                URLConnection connection = crlUrl.openConnection();
                connection.setDoInput(true);
                connection.setUseCaches(false);
                DataInputStream inStream =
                        new DataInputStream(connection.getInputStream());
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                X509CRL crl = (X509CRL)cf.generateCRL(inStream);
                inStream.close();
                params.addCertStore(CertStore.getInstance("Collection",
                        new CollectionCertStoreParameters(
                                Collections.singletonList(crl))));
            }

            // perform validation
            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            PKIXCertPathValidatorResult cpvResult = (PKIXCertPathValidatorResult) cpv
                    .validate(cp, params);
            X509Certificate trustedCert = (X509Certificate) cpvResult
                    .getTrustAnchor().getTrustedCert();

            if (trustedCert == null) {
                log.debug("Trusted Cert = NULL");
            } else {
                log.debug("Trusted CA DN = " + trustedCert.getSubjectDN());
            }

        } catch (CertPathValidatorException e) {
            log.error(e, e);
            throw new X509CertificateValidationException(e);
        } catch (Exception e) {
            log.error(e, e);
            throw new X509CertificateValidationException(e);
        }
        log.debug("CERTIFICATE VALIDATION SUCCEEDED");
    }
}
