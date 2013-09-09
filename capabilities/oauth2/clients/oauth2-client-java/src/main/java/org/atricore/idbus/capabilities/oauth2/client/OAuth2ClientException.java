package org.atricore.idbus.capabilities.oauth2.client;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class OAuth2ClientException extends Exception {

    public OAuth2ClientException() {
    }

    public OAuth2ClientException(String message) {
        super(message);
    }

    public OAuth2ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2ClientException(Throwable cause) {
        super(cause);
    }
}
