package org.atricore.idbus.capabilities.csca.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.security.Security;
import java.security.cert.*;
import java.util.HashSet;
import java.util.Set;

/**
 * OCSP X509 Certificate validator.
 *
 * @org.apache.xbean.XBean element="ocsp-validator"
 */
public class OCSPX509CertificateValidator extends AbstractX509CertificateValidator {

    private static final Log log = LogFactory
            .getLog(OCSPX509CertificateValidator.class);

    private String _ocspResponderCertificateAlias;
    private X509Certificate _ocspCert;

    @Override
    public synchronized void initialize() {

        if (isInitialized())
            return;

        super.initialize();

        // activate OCSP
        Security.setProperty("ocsp.enable", "true");

        if (_url != null) {
            log.debug("Using the OCSP server at: " + _url);
            Security.setProperty("ocsp.responderURLocsp.responderURL", _url);
        } else {
            log.debug("Using the OCSP server specified in the " +
                    "Authority Info Access (AIA) extension " +
                    "of the certificate");
        }
    }

    public void validate(X509Certificate certificate)
            throws X509CertificateValidationException {

        try {


            // get certificate path
            CertPath cp = generateCertificatePath(certificate);

            // get trust anchors
            Set<TrustAnchor> trustedCertsSet = generateTrustAnchors();

            // init PKIX parameters
            PKIXParameters params = new PKIXParameters(trustedCertsSet);

            // init cert store
            Set<X509Certificate> certSet = new HashSet<X509Certificate>();
            if (_ocspCert == null) {
                _ocspCert = getCertificate(_ocspResponderCertificateAlias);
            }
            if (_ocspCert != null) {
                certSet.add(_ocspCert);
                CertStoreParameters storeParams = new CollectionCertStoreParameters(
                        certSet);
                CertStore store = CertStore.getInstance("Collection", storeParams);
                params.addCertStore(store);
                Security.setProperty("ocsp.responderCertSubjectName", _ocspCert
                        .getSubjectX500Principal().getName());
            }

            // activate certificate revocation checking
            params.setRevocationEnabled(true);

            // perform validation
            log.debug("Performing validation for " + certificate.getSubjectDN());

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
            log.debug(e, e);
            throw new X509CertificateValidationException(e);
        } catch (Exception e) {
            log.error(e, e);
            throw new X509CertificateValidationException(e);
        }
        log.debug("CERTIFICATE VALIDATION SUCCEEDED for " + certificate.getSubjectDN());
    }

    /**
     * @return the ocspResponderCertificateAlias
     */
    public String getOcspResponderCertificateAlias() {
        return _ocspResponderCertificateAlias;
    }

    /**
     * @param ocspResponderCertificateAlias the ocspResponderCertificateAlias to set
     */
    public void setOcspResponderCertificateAlias(
            String ocspResponderCertificateAlias) {
        _ocspResponderCertificateAlias = ocspResponderCertificateAlias;
    }
}
