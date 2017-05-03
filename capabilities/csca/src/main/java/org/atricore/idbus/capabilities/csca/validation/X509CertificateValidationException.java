package org.atricore.idbus.capabilities.csca.validation;

/**
 * X509 Certificate exception.
 */
public class X509CertificateValidationException extends Exception {

    private static final long serialVersionUID = 1L;

    public X509CertificateValidationException() {
        super();
    }

    public X509CertificateValidationException(String message) {
        super(message);
    }

    public X509CertificateValidationException(Throwable cause) {
        super(cause);
    }

    public X509CertificateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
