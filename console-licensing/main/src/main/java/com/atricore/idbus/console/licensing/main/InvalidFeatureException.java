package com.atricore.idbus.console.licensing.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InvalidFeatureException extends Exception {

    public InvalidFeatureException() {
        super();
    }

    public InvalidFeatureException(String message) {
        super(message);
    }

    public InvalidFeatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFeatureException(Throwable cause) {
        super(cause);
    }
}
