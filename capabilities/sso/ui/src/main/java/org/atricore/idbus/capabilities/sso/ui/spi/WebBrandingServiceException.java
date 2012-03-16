package org.atricore.idbus.capabilities.sso.ui.spi;

/**
 * @author <a href=mailto:sgonzalez@atricore.org>Sebastian Gonzalez Oyuela</a>
 */
public class WebBrandingServiceException extends Exception {
    public WebBrandingServiceException() {
    }

    public WebBrandingServiceException(String message) {
        super(message);
    }

    public WebBrandingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebBrandingServiceException(Throwable cause) {
        super(cause);
    }
}


