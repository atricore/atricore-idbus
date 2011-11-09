package org.atricore.idbus.capabilities.oauth2.main;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2Exception extends Exception {

    public OAuth2Exception() {
    }

    public OAuth2Exception(String s) {
        super(s);
    }

    public OAuth2Exception(String s, Throwable throwable) {
        super(s, throwable);
    }

    public OAuth2Exception(Throwable throwable) {
        super(throwable);
    }
}
