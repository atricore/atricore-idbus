package org.atricore.idbus.kernel.main.store.exceptions;

/**
 *
 */
public class InvalidCredentialsException extends SSOIdentityException {

    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(Throwable cause) {
        super(cause);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
