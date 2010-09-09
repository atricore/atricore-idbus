package com.atricore.idbus.console.lifecycle.main.exception;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ApplinaceValidationException extends IdentityServerException {
    public ApplinaceValidationException() {
        super();
    }

    public ApplinaceValidationException(String message) {
        super(message);
    }

    public ApplinaceValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplinaceValidationException(Throwable cause) {
        super(cause);
    }
}
