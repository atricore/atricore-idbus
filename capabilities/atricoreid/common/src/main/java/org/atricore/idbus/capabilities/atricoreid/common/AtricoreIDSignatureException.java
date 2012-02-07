package org.atricore.idbus.capabilities.atricoreid.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class AtricoreIDSignatureException extends Exception {

    public AtricoreIDSignatureException() {
    }

    public AtricoreIDSignatureException(String s) {
        super(s);
    }

    public AtricoreIDSignatureException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public AtricoreIDSignatureException(Throwable throwable) {
        super(throwable);
    }
}
