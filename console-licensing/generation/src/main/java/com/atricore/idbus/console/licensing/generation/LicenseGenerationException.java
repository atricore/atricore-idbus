package com.atricore.idbus.console.licensing.generation;

/**
 * Author: Dejan Maric
 */
public class LicenseGenerationException extends Exception {

        public LicenseGenerationException() {
    }

    public LicenseGenerationException(String message) {
        super(message);
    }

    public LicenseGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LicenseGenerationException(Throwable cause) {
        super(cause);
    }
}
