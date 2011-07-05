package com.atricore.liveservices.liveupdate._1_0.util;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class InvalidSignatureException extends Exception {

    public InvalidSignatureException() {
        super();
    }

    public InvalidSignatureException(String message) {
        super(message);
    }

    public InvalidSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSignatureException(Throwable cause) {
        super(cause);
    }
}
