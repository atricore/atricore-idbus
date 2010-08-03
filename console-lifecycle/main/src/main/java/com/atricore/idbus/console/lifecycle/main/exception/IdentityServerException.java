package com.atricore.idbus.console.lifecycle.main.exception;

/**
 * Author: Dejan Maric
 */
public class IdentityServerException extends Exception {

	public IdentityServerException() {
    }

    public IdentityServerException(String message) {
        super(message);
    }

    public IdentityServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentityServerException(Throwable cause) {
        super(cause);
    }
}
