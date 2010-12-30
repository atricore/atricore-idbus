package com.atricore.idbus.console.licensing.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InvalidLicenseException extends Exception {

    public InvalidLicenseException() {
    }

    public InvalidLicenseException(String message) {
        super(message);
    }

    public InvalidLicenseException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLicenseException(Throwable cause) {
        super(cause);
    }
}
