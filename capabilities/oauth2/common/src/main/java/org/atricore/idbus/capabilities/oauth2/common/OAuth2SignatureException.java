package org.atricore.idbus.capabilities.oauth2.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2SignatureException extends Exception {

    public OAuth2SignatureException() {
    }

    public OAuth2SignatureException(String s) {
        super(s);
    }

    public OAuth2SignatureException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OAuth2SignatureException(Throwable throwable) {
        super(throwable);
    }
}
