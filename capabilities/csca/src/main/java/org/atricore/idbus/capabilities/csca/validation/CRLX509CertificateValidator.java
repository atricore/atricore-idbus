package org.atricore.idbus.capabilities.csca.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.DataInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Security;
import java.security.cert.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * CRL X509 Certificate validator.
 *
 * @org.apache.xbean.XBean element="crl-validator"
 *
 * @deprecated
 */
public class CRLX509CertificateValidator extends AbstractX509CertificateValidator {

    private X509CRL clr;

    private long lastRefresh = 0;

    private long refreshInterval = 24L * 60L * 60L * 1000L;

    private static final Log log = LogFactory
            .getLog(CRLX509CertificateValidator.class);

    @Override
    public synchronized void initialize() {

        if (isInitialized())
            return;

        super.initialize();

        if (_url != null) {
            log.debug("Using the CRL server at: " + _url);

        } else {
            log.debug("Using the CRL server specified in the certificate.");
            System.setProperty("com.sun.security.enableCRLDP", "true");
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

            // activate certificate revocation checking
            params.setRevocationEnabled(true);

            // disable OCSP
            // Security.setProperty("ocsp.enable", "false");

            // get a certificate revocation list

            if (_url != null) {

                if (clr == null  || this.lastRefresh + this.refreshInterval < System.currentTimeMillis()) {

                    log.debug("Loading CRL from " + _url);

                    URL crlUrl = new URL(_url);

                    URLConnection connection = crlUrl.openConnection();
                    connection.setDoInput(true);
                    connection.setUseCaches(false);
                    DataInputStream inStream = new DataInputStream(connection.getInputStream());
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
                    this.clr = (X509CRL) cf.generateCRL(inStream);
                    this.lastRefresh = System.currentTimeMillis();
                    inStream.close();

                    log.debug("Loaded CRL : " + this.clr.getIssuerX500Principal());
                }

                params.addCertStore(CertStore.getInstance("Collection",
                        new CollectionCertStoreParameters(
                                Collections.singletonList(this.clr))));
            }

            // perform validation
            log.debug("Performing validation for " + certificate.getSubjectDN());

            CertPathValidator cpv = CertPathValidator.getInstance("PKIX");
            PKIXCertPathValidatorResult cpvResult = (PKIXCertPathValidatorResult) cpv.validate(cp, params);
            X509Certificate trustedCert = cpvResult.getTrustAnchor().getTrustedCert();

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

    public X509CRL getClr() {
        return clr;
    }

    public void setClr(X509CRL clr) {
        this.clr = clr;
    }

    public long getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(long lastRefresh) {
        this.lastRefresh = lastRefresh;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }
}
