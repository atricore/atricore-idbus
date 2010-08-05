package com.atricore.idbus.console.activation.main.exception;

/**
 * @author <a href=mailto:sgonzalez@atricor.org>Sebastian Gonzalez Oyuela</a>
 */
public class ActivationException extends Exception {

    public ActivationException() {
        super();
    }

    public ActivationException(String message) {
        super(message);
    }

    public ActivationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivationException(Throwable cause) {
        super(cause);
    }
}
