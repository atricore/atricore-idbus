package org.atricore.idbus.capabilities.spmlr2.command.util;

public class UserParseException extends Exception {

    public UserParseException() {
        super();
    }

    public UserParseException(String message) {
        super(message);
    }

    public UserParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserParseException(Throwable cause) {
        super(cause);
    }

    protected UserParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
