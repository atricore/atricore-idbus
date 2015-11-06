package org.atricore.idbus.capabilities.oauth2.common;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2EncryptionException extends Exception {

    public OAuth2EncryptionException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public OAuth2EncryptionException(String s) {
        super(s);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public OAuth2EncryptionException(String s, Throwable throwable) {
        super(s, throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public OAuth2EncryptionException(Throwable throwable) {
        super(throwable);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
