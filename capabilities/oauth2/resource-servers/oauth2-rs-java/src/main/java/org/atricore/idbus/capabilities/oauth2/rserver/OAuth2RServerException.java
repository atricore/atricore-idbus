package org.atricore.idbus.capabilities.oauth2.rserver;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2RServerException extends Exception {
    public OAuth2RServerException() {
    }

    public OAuth2RServerException(String s) {
        super(s);
    }

    public OAuth2RServerException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OAuth2RServerException(Throwable throwable) {
        super(throwable);
    }
}
