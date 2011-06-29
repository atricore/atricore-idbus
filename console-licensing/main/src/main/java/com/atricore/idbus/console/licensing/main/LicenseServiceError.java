package com.atricore.idbus.console.licensing.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LicenseServiceError extends Exception {
    public LicenseServiceError() {
        super();
    }

    public LicenseServiceError(String message) {
        super(message);
    }

    public LicenseServiceError(String message, Throwable cause) {
        super(message, cause);
    }

    public LicenseServiceError(Throwable cause) {
        super(cause);
    }
}
