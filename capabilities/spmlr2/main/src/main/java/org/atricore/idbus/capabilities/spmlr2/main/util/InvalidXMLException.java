package org.atricore.idbus.capabilities.spmlr2.main.util;

public class InvalidXMLException extends Exception {

    public InvalidXMLException() {
        super();
    }

    public InvalidXMLException(String message) {
        super(message);
    }

    public InvalidXMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidXMLException(Throwable cause) {
        super(cause);
    }

    protected InvalidXMLException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
