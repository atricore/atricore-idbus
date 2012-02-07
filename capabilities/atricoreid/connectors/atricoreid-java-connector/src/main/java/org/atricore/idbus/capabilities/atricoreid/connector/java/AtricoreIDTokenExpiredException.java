package org.atricore.idbus.capabilities.atricoreid.connector.java;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDTokenExpiredException extends AtricoreIDRServerException {

    public AtricoreIDTokenExpiredException() {
        super();
    }

    public AtricoreIDTokenExpiredException(String s) {
        super(s);
    }

    public AtricoreIDTokenExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AtricoreIDTokenExpiredException(Throwable throwable) {
        super(throwable);
    }
}
