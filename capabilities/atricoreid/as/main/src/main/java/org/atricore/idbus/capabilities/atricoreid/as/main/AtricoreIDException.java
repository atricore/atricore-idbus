package org.atricore.idbus.capabilities.atricoreid.as.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDException extends Exception {

    public AtricoreIDException() {
    }

    public AtricoreIDException(String s) {
        super(s);
    }

    public AtricoreIDException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AtricoreIDException(Throwable throwable) {
        super(throwable);
    }
}
