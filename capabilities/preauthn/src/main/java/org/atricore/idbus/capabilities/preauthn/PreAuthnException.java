package org.atricore.idbus.capabilities.preauthn;

public class PreAuthnException extends Exception {

    public PreAuthnException() {
        super();
    }

    public PreAuthnException(String message) {
        super(message);
    }

    public PreAuthnException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreAuthnException(Throwable cause) {
        super(cause);
    }

    protected PreAuthnException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
