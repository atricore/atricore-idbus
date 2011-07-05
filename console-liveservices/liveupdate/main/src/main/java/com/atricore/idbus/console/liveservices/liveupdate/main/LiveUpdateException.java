package com.atricore.idbus.console.liveservices.liveupdate.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class LiveUpdateException extends Exception {
    public LiveUpdateException() {
        super();
    }

    public LiveUpdateException(String message) {
        super(message);
    }

    public LiveUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public LiveUpdateException(Throwable cause) {
        super(cause);
    }
}
