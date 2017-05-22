package org.atricore.idbus.capabilities.csca.validation;

import java.security.cert.X509Certificate;

/**
 * X509 Certificate validator.
 */
public interface X509CertificateValidator {

    /**
     * Checks is certificate is valid.
     *
     * @param certificate certificate
     */
    void validate(X509Certificate certificate) throws X509CertificateValidationException;
}
