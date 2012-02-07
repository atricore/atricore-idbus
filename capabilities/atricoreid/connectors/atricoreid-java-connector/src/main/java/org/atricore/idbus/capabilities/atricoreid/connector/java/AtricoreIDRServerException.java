package org.atricore.idbus.capabilities.atricoreid.connector.java;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDRServerException extends Exception {
    public AtricoreIDRServerException() {
    }

    public AtricoreIDRServerException(String s) {
        super(s);
    }

    public AtricoreIDRServerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AtricoreIDRServerException(Throwable throwable) {
        super(throwable);
    }
}
