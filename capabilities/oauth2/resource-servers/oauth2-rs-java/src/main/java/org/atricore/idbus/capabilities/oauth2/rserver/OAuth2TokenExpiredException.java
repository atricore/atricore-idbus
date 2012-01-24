package org.atricore.idbus.capabilities.oauth2.rserver;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2TokenExpiredException extends OAuth2RServerException {

    public OAuth2TokenExpiredException() {
        super();
    }

    public OAuth2TokenExpiredException(String s) {
        super(s);
    }

    public OAuth2TokenExpiredException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OAuth2TokenExpiredException(Throwable throwable) {
        super(throwable);
    }
}
